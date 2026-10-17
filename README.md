<div align="center">

# 📚 BookSwap

### A full-stack platform to swap books instead of buying new ones browse, request, chat and exchange

![React](https://img.shields.io/badge/React-18-61DAFB?logo=react&logoColor=white)
![Vite](https://img.shields.io/badge/Vite-646CFF?logo=vite&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3-6DB33F?logo=springboot&logoColor=white)
![Java](https://img.shields.io/badge/Java-17%2F21-007396?logo=openjdk&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-47A248?logo=mongodb&logoColor=white)
![JWT](https://img.shields.io/badge/Auth-JWT-000000?logo=jsonwebtokens&logoColor=white)
![WebSocket](https://img.shields.io/badge/Realtime-WebSocket%20(STOMP)-FF6600?logo=websocket&logoColor=white)
![Recharts](https://img.shields.io/badge/Charts-Recharts-22B5BF)

</div>

---

## ✨ Overview

**BookSwap** is a full-stack web application that lets students and book lovers swap books instead of buying new ones. Users list the books they own, browse and search listings by title, author, category and city, and send an exchange request offering one of their own books in return. The owner can accept or reject the request; once accepted, both books are marked exchanged, the two users chat in real time to arrange the handover, and afterwards they rate each other.



---

## 🎯 Features

### 🙋 For users

| | Feature |
|---|---|
| 🔐 | **Accounts** — register and log in securely with JWT; passwords hashed with BCrypt |
| 📖 | **Book listings** — add, edit, delete books with title, author, category, condition and description |
| 🖼️ | **Flexible photos** — upload a book photo from your device **or** paste an external image link |
| 🔎 | **Search, filter & sort** — by title, author, category, city and condition, with pagination |
| 🔄 | **Exchange requests** — offer one of your own books for someone else's, with full history |
| ✅ | **Accept / reject** — book owners approve or decline requests; other pending requests auto-resolve |
| 💬 | **Real-time chat** — WebSocket (STOMP) chat with the other user once a request is accepted |
| ⭐ | **Ratings & reviews** — rate and review the other user after a completed exchange |
| ❤️ | **Wishlist** — save books of interest to view later |
| 🔔 | **Notifications** — in-app and email alerts for requests, acceptances, rejections and reviews |
| 👤 | **Profile** — manage your own details and see your average rating |

### 🛠️ For admins

| | Feature |
|---|---|
| 📊 | **Graphical dashboard** — bar and pie charts for users, books, exchanges and categories (Recharts) |
| 🙍 | **Manage users** — block / unblock accounts |
| 🗂️ | **Manage categories** — add, edit, remove book categories |
| 📚 | **Manage books** — moderate or remove any listing |

### 🔒 Security & platform

- Stateless **JWT authentication** with role-based access (`USER` / `ADMIN`) on every protected endpoint
- **BCrypt** password hashing; request validation with Bean Validation (`@Valid`)
- Ownership checks so users can only modify their own books and requests
- **CORS** restricted to the frontend origin; secrets kept out of source code
- Global exception handling no internal stack traces leaked to clients
- **Privacy Policy**, **Terms of Use** and **Cookies Notice** pages with a cookie-consent banner and saved preferences

---

## 🧰 Tech stack

| Layer | Technologies |
|---|---|
| **Frontend** | React 18 (Vite), React Router, Axios, Context API, Recharts, lucide-react, react-hot-toast |
| **Backend** | Java 17/21, Spring Boot 3, Spring Web, Spring Security, Spring Data MongoDB, Spring WebSocket (STOMP) |
| **Database** | MongoDB |
| **Auth & security** | JSON Web Tokens (jjwt), BCrypt |
| **Email** | JavaMailSender |
| **Testing** | JUnit 5, Mockito |
| **Build tools** | Maven (backend), npm / Vite (frontend) |

---

## 📁 Project Structure

```
book-exchange-portal/
├── backend/                          # Spring Boot REST API (Maven)
│   └── src/main/java/com/bookexchange/
│       ├── entity/                    # MongoDB documents (User, Book, Exchange, ...)
│       ├── repository/                # Spring Data MongoDB repositories
│       ├── dto/                       # Request / response objects
│       ├── security/                  # JWT utilities, auth filter, security config
│       ├── config/                    # WebSocket config, file-upload config, data seeder
│       ├── service/                   # Business logic
│       └── controller/                # REST controllers
│   └── src/main/resources/application.properties
├── frontend/                         # React + Vite app
│   └── src/
│       ├── components/                # Navbar, Footer, BookCard, CookieBanner, ...
│       ├── pages/                     # Home, Books, BookDetails, Admin, Chat, ...
│       ├── context/                   # Auth, Category, Wishlist contexts
│       └── api.js                     # Axios instance (adds the JWT automatically)
├── scripts/
│   └── import-books.mjs              # Bulk-import sample books via the REST API
├── books-seed.csv                    # 30 sample books with real cover images
└── README.md
```

## 🚀 Quick start

### Prerequisites

- **JDK 17 or 21** (`java -version`)
- **Maven** (`mvn -version`)
- **Node.js 18+** and npm (`node -v`, `npm -v`)
- **MongoDB** — [MongoDB Community Server](https://www.mongodb.com/try/download/community) running locally (or a free [MongoDB Atlas](https://www.mongodb.com/cloud/atlas) cluster)

### 1️⃣ Start MongoDB

```bash
mongod
```

### 2️⃣ Backend

```bash
cd backend
mvn spring-boot:run
```

The API runs at **http://localhost:8080**. On first run, a default admin account and starter categories are seeded automatically.

### 3️⃣ Frontend

In a **second terminal**:

```bash
cd frontend
npm install
npm run dev
```

Open **http://localhost:5173** 🎉

### 4️⃣ (Optional) Import sample books

Populate the catalogue with 30 real books, complete with cover images:

```bash
cd scripts
node import-books.mjs
```

### 5️⃣ Demo account (seeded automatically)

| Role | Email | Password |
|---|---|---|
| 👑 Admin | `admin@bookexchange.com` | `Admin@123` |



---

## 🔧 Configuration

### `backend/src/main/resources/application.properties`

| Property | Required | Description |
|---|---|---|
| `spring.data.mongodb.uri` | ✅ | MongoDB connection string. Local default: `mongodb://localhost:27017/book_exchange` |
| `jwt.secret` | ✅ | Long random string used to sign login tokens |
| `jwt.expiration` | – | Token lifetime in milliseconds |
| `cors.allowed-origin` | ✅ | Frontend URL allowed to call the API (default `http://localhost:5173`) |
| `app.admin.email` / `app.admin.password` | – | Credentials used to seed the default admin account |
| `spring.mail.*` | for email | SMTP settings for sending notification emails (disabled by default) |
| `file.upload-dir` | – | Local folder where uploaded book photos are stored |

> 🔁 Restart the backend after changing `application.properties`. Never commit real secrets — keep them local only.

---

## 📡 API reference

<details>
<summary><b>Click to expand all endpoints</b></summary>

<br>

Base URL: `http://localhost:8080/api` — protected routes expect `Authorization: Bearer <token>`.

| Method | Endpoint | Description | Access |
|---|---|---|---|
| POST | `/auth/register` | Register a new user | Public |
| POST | `/auth/login` | Log in and receive a JWT | Public |
| GET | `/users/me` | Get own profile | User |
| PUT | `/users/me` | Update own profile | User |
| GET | `/books` | List available books (paginated) | Public |
| GET | `/books/{id}` | Get book details | Public |
| GET | `/books/search` | Search / filter by keyword, category, city | Public |
| GET | `/books/my` | List own books | User |
| POST | `/books` | Add a book | User |
| PUT | `/books/{id}` | Update a book | Owner |
| DELETE | `/books/{id}` | Delete a book | Owner, Admin |
| GET | `/categories` | List categories | Public |
| POST / PUT / DELETE | `/categories` | Manage categories | Admin |
| POST | `/exchanges` | Send an exchange request | User |
| GET | `/exchanges/sent` | Requests sent by me | User |
| GET | `/exchanges/received` | Requests received by me | User |
| PUT | `/exchanges/{id}/accept` | Accept a request | Owner |
| PUT | `/exchanges/{id}/reject` | Reject a request | Owner |
| GET | `/messages/{exchangeId}` | Chat history of an exchange | Participants |
| WS | `/ws` (STOMP) | Real-time chat channel | Participants |
| POST | `/reviews` | Rate and review after an exchange | User |
| GET | `/users/{id}/reviews` | Reviews received by a user | User |
| GET | `/wishlist` | View wishlist | User |
| POST / DELETE | `/wishlist/{bookId}` | Add / remove a wishlist item | User |
| GET | `/notifications` | List notifications | User |
| PUT | `/notifications/{id}/read` | Mark notification as read | User |
| GET | `/admin/users` | List all users | Admin |
| PUT | `/admin/users/{id}/block` | Block / unblock a user | Admin |
| GET | `/admin/stats` | Dashboard statistics | Admin |

</details>

---

## 🧭 How it works

- **Authentication** — on login the backend returns a signed JWT. The frontend stores it and the Axios instance in `src/api.js` attaches it to every request; `JwtAuthFilter` validates it on the backend and enforces `USER` / `ADMIN` roles.
- **Exchange workflow** — a request starts as `PENDING`. The owner can accept (both books become `EXCHANGED`, every other pending request on either book is auto-rejected, and a chat channel opens) or reject (the books stay `AVAILABLE`).
- **Chat** — once a request is accepted, both participants can exchange messages over a STOMP WebSocket channel; history is stored so it reloads correctly.
- **Admin dashboard** — `/admin/stats` returns aggregate counts, rendered as bar and pie charts on the admin page.
- **Cookies** — the cookie banner gates non-essential storage until the visitor accepts, with a preferences page to change the choice later.

---

## 🩹 Troubleshooting

| Problem | Fix |
|---|---|
| `JAVA_HOME is not defined correctly` | Set `JAVA_HOME` to your JDK install folder (e.g. `C:\Program Files\Java\jdk-21...`) and reopen the terminal |
| `No plugin found for prefix 'spring_boot'` | Typo — the Maven goal uses a hyphen: `mvn spring-boot:run`, not `spring_boot:run` |
| MongoDB connection error | Make sure `mongod` is running locally, or that your Atlas URI and IP allow-list are correct |
| Frontend shows "Network Error" | Check that the backend is running on port 8080 and `cors.allowed-origin` matches the frontend URL |
| 401 Unauthorized on every request | Token missing/expired — log in again; check `jwt.secret` hasn't changed between restarts |
| Blocked user can't log in | Expected — an admin must unblock the account from the Admin dashboard |
| Uploaded photo doesn't show | Check `file.upload-dir` exists and is writable, and that the backend serves `/uploads/**` |

---

## 📌 Current scope

- 📦 Exchanges are strictly **book-for-book**; there is no payment, pricing or courier integration — users arrange the physical handover themselves via chat.
- 🖼️ Book photos are either uploaded locally or linked externally; there is no cloud storage integration.
- ✉️ Email notifications require SMTP credentials to be configured — they are disabled by default in a fresh clone.

---


---

<div align="center">

Designed and Developed by **Pratimesh Tonde** ❤️ | Powered by **AIX Tech Ltd.**

⭐ If you like this project, give it a star!

</div>
