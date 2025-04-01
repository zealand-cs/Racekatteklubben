package dk.zealandcs.racekatteklubben.domain;

public enum Role {
    User(1),
    Employee(2),
    Admin(3);
    
    private final int rank;

    Role(int rank) { this.rank = rank; }

    public boolean isAtLeast(Role role) { return this.rank >= role.rank; }
}
