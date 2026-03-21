#!/usr/bin/env bash
# ── Naruto Tower Defense — build and run (Linux / macOS / Git Bash) ────────
# Usage:  ./run.sh
# Builds a self-contained fat jar via Maven Shade, then starts the server.
# The game will be available at http://localhost:8080

set -e

echo "[1/2] Building fat jar..."
mvn package -q

echo "[2/2] Starting server..."
echo "      Open http://localhost:8080 in your browser."
echo "      Press Ctrl+C to stop."
echo ""

java -jar target/naruto-tower-defense-1.0-SNAPSHOT.jar
