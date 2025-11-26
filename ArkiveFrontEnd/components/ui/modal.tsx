"use client"

import * as React from "react"
import * as DialogPrimitive from "@radix-ui/react-dialog"
import { X } from "lucide-react"
import { cn } from "@/lib/utils"

export type ModalSize = "sm" | "md" | "lg" | "xl" | "full"

const sizeToClass: Record<ModalSize, string> = {
    sm: "sm:max-w-[28rem]",
    md: "sm:max-w-[36rem]",
    lg: "sm:max-w-[48rem]",
    xl: "sm:max-w-[64rem]",
    full: "sm:max-w-[90vw] sm:h-auto",
}

export const Modal = DialogPrimitive.Root
export const ModalTrigger = DialogPrimitive.Trigger
export const ModalPortal = DialogPrimitive.Portal
export const ModalClose = DialogPrimitive.Close

export interface ModalContentProps
    extends React.ComponentPropsWithoutRef<typeof DialogPrimitive.Content> {
    size?: ModalSize
    hideClose?: boolean
}

export const ModalOverlay = React.forwardRef<
    React.ElementRef<typeof DialogPrimitive.Overlay>,
    React.ComponentPropsWithoutRef<typeof DialogPrimitive.Overlay>
>(({ className, ...props }, ref) => (
    <DialogPrimitive.Overlay
        ref={ref}
        className={cn(
            "fixed inset-0 z-50 bg-black/60 backdrop-blur-sm data-[state=open]:animate-in data-[state=closed]:animate-out data-[state=closed]:fade-out-0 data-[state=open]:fade-in-0",
            className
        )}
        {...props}
    />
))
ModalOverlay.displayName = DialogPrimitive.Overlay.displayName

export const ModalContent = React.forwardRef<
    React.ElementRef<typeof DialogPrimitive.Content>,
    ModalContentProps
>(({ className, size = "md", hideClose = false, children, ...props }, ref) => (
    <ModalPortal>
        <ModalOverlay />
        <DialogPrimitive.Content
            ref={ref}
            className={cn(
                "fixed left-1/2 top-1/2 z-50 grid w-[90vw] -translate-x-1/2 -translate-y-1/2 gap-4 border bg-background p-6 text-foreground shadow-lg duration-200 data-[state=open]:animate-in data-[state=closed]:animate-out data-[state=closed]:fade-out-0 data-[state=open]:fade-in-0 data-[state=closed]:zoom-out-95 data-[state=open]:zoom-in-95 data-[state=closed]:slide-out-to-top-[2%] data-[state=open]:slide-in-from-top-[2%] rounded-lg border-border",
                sizeToClass[size],
                className
            )}
            {...props}
        >
            {!hideClose && (
                <DialogPrimitive.Close
                    className={cn(
                        "absolute right-3 top-3 rounded-sm opacity-70 ring-offset-background transition-opacity hover:opacity-100 focus:outline-none focus:ring-2 focus:ring-primary focus:ring-offset-2",
                    )}
                    aria-label="Close"
                >
                    <X className="h-4 w-4" />
                    <span className="sr-only">Close</span>
                </DialogPrimitive.Close>
            )}
            {children}
        </DialogPrimitive.Content>
    </ModalPortal>
))
ModalContent.displayName = DialogPrimitive.Content.displayName

export const ModalHeader = ({ className, ...props }: React.HTMLAttributes<HTMLDivElement>) => (
    <div
        className={cn(
            "flex flex-col space-y-1.5 text-center sm:text-left",
            className
        )}
        {...props}
    />
)

export const ModalFooter = ({ className, ...props }: React.HTMLAttributes<HTMLDivElement>) => (
    <div
        className={cn(
            "flex flex-col-reverse gap-2 sm:flex-row sm:justify-end",
            className
        )}
        {...props}
    />
)

export const ModalTitle = React.forwardRef<
    React.ElementRef<typeof DialogPrimitive.Title>,
    React.ComponentPropsWithoutRef<typeof DialogPrimitive.Title>
>(({ className, ...props }, ref) => (
    <DialogPrimitive.Title
        ref={ref}
        className={cn("text-lg font-semibold leading-none tracking-tight", className)}
        {...props}
    />
))
ModalTitle.displayName = DialogPrimitive.Title.displayName

export const ModalDescription = React.forwardRef<
    React.ElementRef<typeof DialogPrimitive.Description>,
    React.ComponentPropsWithoutRef<typeof DialogPrimitive.Description>
>(({ className, ...props }, ref) => (
    <DialogPrimitive.Description
        ref={ref}
        className={cn("text-sm text-muted-foreground", className)}
        {...props}
    />
))
ModalDescription.displayName = DialogPrimitive.Description.displayName

export interface SimpleModalProps {
    open: boolean
    onOpenChange: (open: boolean) => void
    title?: React.ReactNode
    description?: React.ReactNode
    children?: React.ReactNode
    footer?: React.ReactNode
    size?: ModalSize
    className?: string
    hideClose?: boolean
}

export function SimpleModal({
    open,
    onOpenChange,
    title,
    description,
    children,
    footer,
    size = "md",
    className,
    hideClose,
}: SimpleModalProps) {
    return (
        <Modal open={open} onOpenChange={onOpenChange}>
            <ModalContent size={size} className={className} hideClose={hideClose}>
                {(title || description) && (
                    <ModalHeader>
                        {title ? <ModalTitle>{title}</ModalTitle> : null}
                        {description ? (
                            <ModalDescription>{description}</ModalDescription>
                        ) : null}
                    </ModalHeader>
                )}
                {children}
                {footer ? <ModalFooter>{footer}</ModalFooter> : null}
            </ModalContent>
        </Modal>
    )
}
