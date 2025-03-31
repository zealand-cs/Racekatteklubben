# Systemsequencediagrams

## Create user ([#1](https://github.com/zealand-cs/Racekatteklubben/issues/1))

```mermaid
sequenceDiagram
    actor User
    participant System
    
    User ->> System: Register user
    alt Invalid credentials/email already used
        System ->> User: Invalid registration. Try again.
    else
        System ->> User: Registration complete
    end
```

## Delete user ([#2](https://github.com/zealand-cs/Racekatteklubben/issues/2))

```mermaid
sequenceDiagram
    actor User
    participant System

    User ->> System: Delete user
    alt Invalid permissions
        System ->> User: You're not allowed to delete the user.
    else
        System ->> User: User gets deleted, along with all cats
    end
```

## Edit user ([#3](https://github.com/zealand-cs/Racekatteklubben/issues/3))

```mermaid
sequenceDiagram
    actor User
    participant System

    User ->> System: Edit user
    alt Invalid permissions
        System ->> User: You're not allowed to edit this user.
    else
        System ->> User: User gets modified.
    end
```

## Login ([#4](https://github.com/zealand-cs/Racekatteklubben/issues/4))

```mermaid
sequenceDiagram
    actor User
    participant System

    User ->> System: Login with email and password
    alt Already logged in
        System ->> User: You're already logged in.
    else Invalid credentials
        System ->> User: Invalid credentials. Try again.
    else
        System ->> User: Session given and set
    end
```

## Logout ([#5](https://github.com/zealand-cs/Racekatteklubben/issues/5))

```mermaid
sequenceDiagram
    actor User
    participant System
    
    User ->> System: Logout
    alt Not logged in 
        System ->> User: Already logget out
    else
        System ->> User: Session invalidated
    end
```

## Role assignment ([#6](https://github.com/zealand-cs/Racekatteklubben/issues/6))

```mermaid
sequenceDiagram
    actor User
    participant System
    
    User ->> System: Assign role to user
    alt Not allowed
        System ->> User: You're not allowed to assign a role
    else
        System ->> User: Role assigned to user
    end
```
