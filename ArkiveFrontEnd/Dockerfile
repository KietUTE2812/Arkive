# Builder stage
FROM node:20-alpine AS builder
WORKDIR /app

# Copy lockfiles first for better cache
COPY package*.json ./
COPY yarn.lock ./

# Install build tools (some deps may need them) and install deps (pnpm nếu có, ngược lại npm)
RUN apk add --no-cache bash python3 make g++ || true
RUN npm install --legacy-peer-deps

# Copy source and build
COPY . .
RUN npm run build

# Production stage (uses Next.js standalone output)
FROM node:20-alpine AS runner
WORKDIR /app

ARG NEXT_PUBLIC_API_URL
ENV NEXT_PUBLIC_API_URL=$NEXT_PUBLIC_API_URL

ARG NEXT_PUBLIC_GOOGLE_CLIENT_ID
ENV NEXT_PUBLIC_GOOGLE_CLIENT_ID=$NEXT_PUBLIC_GOOGLE_CLIENT_ID

ARG NEXT_PUBLIC_R2_PUBLIC_URL
ENV NEXT_PUBLIC_R2_PUBLIC_URL=$NEXT_PUBLIC_R2_PUBLIC_URL

# Copy standalone server and static assets produced by Next.js build
COPY --from=builder /app/.next/standalone/ ./
COPY --from=builder /app/.next/static ./.next/static
COPY --from=builder /app/public ./public

# Run as non-root user provided by node image
USER node

EXPOSE 3000

# Entrypoint starts the Next standalone server
CMD ["node", "server.js"]