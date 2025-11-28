# Builder stage
FROM node:20-alpine AS builder
WORKDIR /app

# Copy lockfiles first for better cache
COPY package*.json ./
COPY yarn.lock ./

RUN npm ci --legacy-peer-deps && npm install sharp --legacy-peer-deps

# Install build tools (some deps may need them) and install deps (pnpm nếu có, ngược lại npm)
RUN apk add --no-cache bash python3 make g++ || true
RUN npm install --legacy-peer-deps

# Copy source and build
COPY . .
RUN npm run build

# Production stage (uses Next.js standalone output)
FROM node:20-alpine AS runner
WORKDIR /app

ENV NODE_ENV=production

# Copy standalone server and static assets produced by Next.js build
COPY --from=builder /app/.next/standalone/ ./
COPY --from=builder /app/.next/static ./.next/static
COPY --from=builder /app/public ./public
COPY --from=builder /app/node_modules ./node_modules

# Run as non-root user provided by node image
USER node

EXPOSE 3000

# Entrypoint starts the Next standalone server
CMD ["node", "server.js"]