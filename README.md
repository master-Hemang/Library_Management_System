# Library_Management_System

A comprehensive Library Management System implemented in Java demonstrating Object-Oriented Programming principles, SOLID design, and design patterns.

## Features

### Core Features
- **Book Management**: Add, remove, update, and search books
- **Patron Management**: Register and manage library members
- **Lending Process**: Checkout and return books with transaction tracking
- **Inventory Management**: Track available and borrowed books across branches

### Advanced Features
- **Multi-branch Support**: Manage multiple library branches
- **Book Transfer**: Transfer books between branches
- **Reservation System**: Reserve checked-out books with notifications
- **Recommendation System**: Personalized book recommendations based on borrowing history

## Design Patterns Implemented

1. **Singleton Pattern**: LibraryManagementSystem class ensures single instance
2. **Factory Pattern**: LibraryItemFactory creates different types of library items
3. **Observer Pattern**: Notification system for reservations
4. **Strategy Pattern**: Flexible search and recommendation algorithms

## SOLID Principles Applied

- **Single Responsibility**: Each class has one clear purpose
- **Open/Closed**: Easy to extend with new item types or search strategies
- **Liskov Substitution**: Book properly extends LibraryItem
- **Interface Segregation**: Focused interfaces like SearchStrategy
- **Dependency Inversion**: High-level modules depend on abstractions
