# RateLimiter

A work-in-progress rate limiting service built with **Java, Spring Boot, and Redis**.

The current `main` branch implements a **Redis-backed sliding-window rate limiter** that tracks requests per user and decides whether a new request should be allowed.

> **Project status:** Work in progress. The core sliding-window implementation is available on `main`; additional algorithms and a more extensible Strategy/Factory-based design are being developed separately.

## Why this project?

Rate limiting is a common backend concern used to protect APIs from abuse, accidental traffic spikes, and excessive resource consumption.

This project is an exploration of how rate limiting can be implemented using Redis as shared state, while keeping the application-side API simple.

## Current Implementation

The current implementation uses a **sliding request window** backed by a Redis Sorted Set.

For every user:

1. Requests older than the active time window are removed.
2. Redis counts the requests still inside the window.
3. If the request count has reached the configured limit, the request is rejected.
4. Otherwise, the current request is inserted with the current timestamp as its score.
5. The Redis key is given an expiry so inactive rate-limit data is eventually cleaned up.

The Redis operations are executed through a **Lua script**, keeping the cleanup, count, limit check, and insert logic together in Redis.

### Current limits

On the `main` branch:

- **Limit:** 100 requests
- **Window:** 60 seconds
- **Redis key expiry:** 5 minutes
- **Identity:** `userId`

## Tech Stack

- **Java 17**
- **Spring Boot 4**
- **Spring Web MVC**
- **Spring Data Redis**
- **Redis**
- **Lua scripting**
- **Gradle**
- **Lombok**
- **JUnit**

## Architecture

```text
Client
  |
  | POST /rate-limiter/v1/api/allow
  v
RateLimiterController
  |
  v
SlidingWindowRateLimiter
  |
  v
RedisService
  |
  | Execute Lua script
  v
Redis Sorted Set
```

Each user is stored under a Redis key similar to:

```text
rate_limit:<userId>
```

Every allowed request is stored as a unique member in the Sorted Set with the request timestamp used as the score.

## API

### Check whether a request is allowed

```http
POST /rate-limiter/v1/api/allow
Content-Type: application/json
```

Request:

```json
{
  "userId": "user-123"
}
```

When the request is allowed:

```http
HTTP/1.1 200 OK
```

```json
{
  "allowed": true
}
```

When the rate limit is exceeded:

```http
HTTP/1.1 403 Forbidden
```

```json
{
  "allowed": false
}
```

## Running Locally

### Prerequisites

Make sure you have:

- Java 17
- Redis running on `localhost:6379`

The application currently expects Redis at:

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
```

### Start Redis

If Redis is installed locally:

```bash
redis-server
```

### Run the application

macOS / Linux:

```bash
./gradlew bootRun
```

Windows:

```bash
gradlew.bat bootRun
```

The application runs on:

```text
http://localhost:8080/rate-limiter/v1
```

### Test the endpoint

```bash
curl -X POST \
  http://localhost:8080/rate-limiter/v1/api/allow \
  -H "Content-Type: application/json" \
  -d '{"userId":"user-123"}'
```

Calling the endpoint repeatedly for the same `userId` will eventually cause requests to be rejected once the active-window limit is reached.

## Sliding-Window Logic

The core Redis script follows this flow:

```text
Remove expired requests
        |
        v
Count requests in current window
        |
        +---- count >= limit ----> Reject
        |
        v
Add current request
        |
        v
Refresh key expiry
        |
        v
Allow
```

A Redis Sorted Set is useful here because request timestamps can be used as scores, allowing old entries to be removed based on time.

## Project Structure

```text
src/main/java/com/lsd/rate_limiter/
├── configuration/
│   └── RedisConfig.java
├── controller/
│   └── RateLimiterController.java
├── dto/
│   ├── CustomRequest.java
│   └── CustomResponse.java
└── service/
    ├── RedisService.java
    └── SlidingWindowRateLimiter.java

src/main/resources/
└── application.yaml
```

## Work in Progress

The project is being expanded beyond the first sliding-window implementation.

Current development work includes exploring:

- **Strategy Pattern** for interchangeable rate-limiting algorithms
- **Factory Pattern** for selecting the appropriate strategy
- **Fixed Window** rate limiting
- **Token Bucket** rate limiting
- Different rate-limit policies for different user plans
- Better testing and edge-case coverage
- Improved API responses and standard rate-limit metadata
- Configuration-driven limits instead of hard-coded values
- Containerized local setup

The goal is to evolve this from a single rate-limiting implementation into a small, extensible rate-limiting service while comparing the trade-offs of different algorithms.

## Notes

This repository is primarily a **learning and system-design project** and is still under active development. The implementation and API may change as additional algorithms, tests, and design improvements are added.
