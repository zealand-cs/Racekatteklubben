# Systemsequencediagrams

## Create user (#1)

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