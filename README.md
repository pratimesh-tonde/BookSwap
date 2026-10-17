# 📚 Book Exchange Portal

A full-stack web application that lets students and book lovers **swap books instead of buying new ones**. List the books you own, browse and search listings by title, author, category and city, send an exchange request offering one of your own books, chat in real time to arrange the handover, and rate each other afterwards.

Built with **React**, a **Spring Boot REST API**, and **MongoDB**, styled with a clean, Amazon/Flipkart-inspired UI.

---

## ✨ Features

- 🔐 **Secure authentication**: JWT-based login/register with role-based access (User / Admin), passwords hashed with BCrypt
- 📖 **Book management**: add, edit, delete books with title, author, category, condition, description and a photo (upload a file **or** paste an external image link)
- 🔍 **Search & filter**: by title, author, category, city and condition, with pagination
- 🔄 **Exchange workflow**: send a request, owner accepts/rejects, both books auto-marked exchanged, other pending requests auto-rejected
- 💬 **Real-time chat**: WebSocket (STOMP) chat between the two users of an accepted exchange
- ⭐ **Ratings & reviews**: rate and review the other user after a completed exchange
- ❤️ **Wishlist**: save books of interest for later
- 🔔 **Notifications**: in-app and email notifications for requests, acceptances, rejections and reviews
- 📊 **Admin dashboard**: graphical statistics (bar + pie charts) for users, books, exchanges and categories; manage/block users, manage categories and books
- 🍪 **Legal pages**: Privacy Policy, Terms of Use, Cookies Notice with a cookie-consent banner and preferences
- 📱 **Responsive design**: works on desktop and mobile

---

## 🛠️ Tech Stack

| Layer      | Technology |
|------------|------------|
| Frontend   | React (Vite), React Router, Axios, Recharts, lucide-react, react-hot-toast |
| Backend    | Java 17/21, Spring Boot 3, Spring Web, Spring Security, Spring Data MongoDB, Spring WebSocket (STOMP) |
| Auth       | JWT (jjwt), BCrypt password hashing |
| Database   | MongoDB |
| Email      | JavaMailSender |
| Testing    | JUnit 5, Mockito |
| Build Tool | Maven (backend), npm/Vite (frontend) |

---

## 📂 Project Structure

```
book-exchange-portal/
├── backend/                  # Spring Boot REST API (Maven)
│   └── src/main/java/com/bookexchange/
│       ├── entity/           # MongoDB documents (User, Book, Exchange, ...)
│       ├── repository/       # Spring Data MongoDB repositories
│       ├── dto/              # Request / response objects
│       ├── security/         # JWT utilities, auth filter, security config
│       ├── config/           # WebSocket config, file-upload config, data seeder
│       ├── service/          # Business logic
│       └── controller/       # REST controllers
│   └── src/main/resources/application.properties
├── frontend/                 # React + Vite app
│   └── src/
│       ├── components/       # Navbar, Footer, BookCard, CookieBanner, ...
│       ├── pages/            # Home, Books, BookDetails, Admin, Chat, ...
│       ├── context/          # Auth, Category, Wishlist contexts
│       └── api.js            # Axios instance
├── scripts/
│   └── import-books.mjs      # Bulk-import sample books via the REST API
├── books-seed.csv            # 30 sample books with real cover images
└── README.md
```

---

## ✅ Prerequisites

- **JDK 17 or 21** (`java -version`)
- **Maven** (`mvn -version`)
- **Node.js (LTS)** and npm (`node -v`, `npm -v`)
- **MongoDB Community Server** (running locally on port `27017`)

---

## 🚀 Getting Started (Local Setup)

### 1. Clone the repository
```bash
git clone https://github.com/pratimesh-tonde>/book-exchange-portal.git
cd book-exchange-portal
```

### 2. Start MongoDB
```bash
mongod
```
The backend connects to `mongodb://localhost:27017/book_exchange` by default.

### 3. Run the backend
```bash
cd backend
mvn spring-boot:run
```
The API starts on **http://localhost:8080**. An admin user and starter categories are seeded automatically on first run.

### 4. Run the frontend
```bash
cd frontend
npm install
npm run dev
```
The app opens on **http://localhost:5173**.

### 5. (Optional) Import sample books
```bash
cd scripts
node import-books.mjs
```

---

## 🔑 Default Admin Login

| Field    | Value |
|----------|-------|
| Email    | `admin@bookexchange.com` |
| Password | `Admin@123` |


---

## 📡 Key API Endpoints

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/auth/register` | Register a new user | Public |
| POST | `/auth/login` | Log in, receive JWT | Public |
| GET | `/books` | List available books (paginated) | Public |
| GET | `/books/search` | Search/filter books | Public |
| POST | `/books` | Add a book | User |
| POST | `/exchanges` | Send an exchange request | User |
| PUT | `/exchanges/{id}/accept` | Accept a request | Owner |
| PUT | `/exchanges/{id}/reject` | Reject a request | Owner |
| WS | `/ws` (STOMP) | Real-time chat channel | Participants |
| POST | `/reviews` | Rate & review after an exchange | User |
| GET | `/admin/stats` | Dashboard statistics | Admin |

---

## 🖼️ Screenshots

### Home
![Home](screenshots/home.png)

### Browse & filter books
![Browse books](screenshots/browse-books.png)

### Book details
![Book details](screenshots/book-details.png)

### Admin dashboard
![Admin dashboard](screenshots/admin-dashboard.png)

### Sign up
![Sign up](screenshots/signup.png)

---

## 🗺️ Future Scope

- AI-based book recommendations
- ISBN scan / auto-fill for faster listing
- Location-based matching with nearby users
- Multi-book / chain exchanges
- Mobile app with push notifications
- College-email based user verification

---

## 📄 License

This project is for academic/educational purposes (Full Stack Java mini project).

---

<p align="center">Designed and Developed by <b>Pratimesh Tonde</b> ❤️ | Powered by <b>AIX Tech Ltd.</b></p>
