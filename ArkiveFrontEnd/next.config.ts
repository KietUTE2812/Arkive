import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  output: "standalone",
  // Next.js 15 with Tailwind CSS v3
  webpack: (config, { isServer }) => {
    // Fix for pdfjs-dist - handle both client and server
    if (isServer) {
      // On the server, mark canvas as external so webpack doesn't try to bundle it
      config.externals = config.externals || [];
      config.externals.push({
        canvas: 'commonjs canvas',
      });
    } else {
      // On the client, alias canvas to false since it's not needed in browser
      config.resolve.alias = {
        ...config.resolve.alias,
        canvas: false,
        encoding: false,
      };
    }
    return config;
  },
  reactStrictMode: false,
  images: {
    remotePatterns: [
      {
        protocol: 'https',
        hostname: 'pub-41a8901c893047ebacf023b74b76bbd5.r2.dev',
        port: '',
        pathname: '/**', // Allows all paths from this host
      },
    ],
  },
  async rewrites() {
    return [
      {
        // Khi Browser gọi: /arkive/api/v1/users
        source: '/arkive/api/:path*',
        // Next.js sẽ gọi ngầm sang: http://backend:8080/api/v1/users
        destination: `${process.env.INTERNAL_API_URL}/arkive/api/:path*`, 
      },
    ];
  },
};

export default nextConfig;
