# ER-diagram

```mermaid
erDiagram
    users {
        int id PK
        string name
        string email
        string password
        string role
        Datetime
    }

    cats {
        int id PK
        int ownerId FK
        string name
        Datetime
        string race
        string imageUrl
        string gender
    }
    
    shows {
        int id PK
        int startDate
        int endDate
    }

    showPlacements {
        int showId PK, FK
        int catId PK, FK
        int showPlacement
    }
```