# Domain model

```mermaid
---
title: Domain model
---
classDiagram
    class User {
        name
        email
        password
        role
    }
    
    class Cat { 
        name
        birthdate
        race
        points
        image
    }
    
    class Show {
        startDate
        endDate
    }
    
    User "1" -- "0..n" Cat
    Cat "0..n" -- "0..n" Show
```