"use client";

import { useEffect, useRef, useState } from "react";
import { Play, Pause, VolumeX, Volume2, Maximize, Minimize } from "lucide-react";
import { cn } from "@/lib/utils";

interface CustomVideoProps {
  src: string;
  poster?: string;
}

export function VideoPreview({ src, poster }: CustomVideoProps) {
  const videoRef = useRef<HTMLVideoElement>(null);

  const [isPlaying, setIsPlaying] = useState(false);
  const [currentTime, setCurrentTime] = useState(0);
  const [duration, setDuration] = useState(0);
  const [volume, setVolume] = useState(1);
  const [isMuted, setIsMuted] = useState(false);
  const [isFullscreen, setIsFullscreen] = useState(false);
  // Update current time
  const onTimeUpdate = () => {
    if (videoRef.current) {
      setCurrentTime(videoRef.current.currentTime);
    }
  };

  // Load metadata -> duration
  const onLoadedMetadata = () => {
    if (videoRef.current) {
      setDuration(videoRef.current.duration);
    }
  };

  // Play / pause toggle
  const togglePlay = () => {
    if (!videoRef.current) return;

    if (videoRef.current.paused) {
      videoRef.current.play();
      setIsPlaying(true);
    } else {
      videoRef.current.pause();
      setIsPlaying(false);
    }
  };

  // Seek (timeline)
  const onSeek = (e: React.ChangeEvent<HTMLInputElement>) => {
    const time = Number(e.target.value);
    if (videoRef.current) {
      videoRef.current.currentTime = time;
      setCurrentTime(time);
    }
  };

  // Volume change
  const onVolumeChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const vol = Number(e.target.value);
    setVolume(vol);
    if (videoRef.current) {
      videoRef.current.volume = vol;
      setIsMuted(vol === 0);
    }
  };

  // Mute / unmute
  const toggleMute = () => {
    if (!videoRef.current) return;

    videoRef.current.muted = !isMuted;
    setIsMuted(!isMuted);
  };

  // Fullscreen
  const requestFullscreen = () => {
    if (isFullscreen) {
      document.exitFullscreen();
      setIsFullscreen(false);
    } else {
      videoRef.current?.parentElement?.requestFullscreen();
      setIsFullscreen(true);
    }
  };

  // Format time (mm:ss)
  const formatTime = (sec: number) => {
    if (!sec) return "00:00";
    const m = Math.floor(sec / 60);
    const s = Math.floor(sec % 60);
    return `${String(m).padStart(2, "0")}:${String(s).padStart(2, "0")}`;
  };

  return (
    <div className="w-full max-w-3xl mx-auto rounded-xl overflow-hidden bg-black relative">

      {/* VIDEO */}
      <video
        ref={videoRef}
        src={src}
        poster={poster}
        className={cn("w-full bg-black", isFullscreen ? "max-h-screen" : "max-h-[70vh]")}
        onTimeUpdate={onTimeUpdate}
        onLoadedMetadata={onLoadedMetadata}
        playsInline
      />

      {/* CUSTOM CONTROLS */}
      <div className="absolute bottom-0 left-0 right-0 bg-black/60 text-white p-3 flex flex-col gap-2">

        {/* Timeline */}
        <input
          type="range"
          min={0}
          max={duration}
          step={0.1}
          value={currentTime}
          onChange={onSeek}
          className="w-full accent-blue-500"
        />

        <div className="flex items-center justify-between">
          {/* LEFT SIDE */}
          <div className="flex items-center gap-3">

            {/* Play button */}
            <button
              onClick={togglePlay}
              className="p-2 hover:bg-white/10 rounded-full transition"
            >
              {isPlaying ? <Pause size={20} /> : <Play size={20} />}
            </button>

            {/* Time */}
            <span className="text-sm opacity-90">
              {formatTime(currentTime)} / {formatTime(duration)}
            </span>

            {/* Volume */}
            <button
              onClick={toggleMute}
              className="p-2 hover:bg-white/10 rounded-full"
            >
              {isMuted || volume === 0 ? <VolumeX size={20} /> : <Volume2 size={20} />}
            </button>

            <input
              type="range"
              min={0}
              max={1}
              step={0.05}
              value={isMuted ? 0 : volume}
              onChange={onVolumeChange}
              className="w-24 accent-blue-500"
            />
          </div>

          {/* RIGHT SIDE */}
          <button
            onClick={requestFullscreen}
            className="p-2 hover:bg-white/10 rounded-full"
          >
            {isFullscreen ? <Minimize size={20} /> : <Maximize size={20} />}
          </button>
        </div>
      </div>
    </div>
  );
}
