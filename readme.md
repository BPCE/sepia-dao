# Sepia QueryDao Framework

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/projects/jdk/21/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-blue.svg)](https://maven.apache.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.0-green.svg)](https://spring.io/projects/spring-boot)
[![JPA](https://img.shields.io/badge/JPA-3.1.0-red.svg)](https://jakarta.ee/specifications/persistence/)

A powerful Java framework that provides a sophisticated abstraction layer over JPA for building dynamic, criteria-based database queries with automatic relationship fetching and query optimization.

## Table of Contents

- [Overview](#overview)
- [Key Features](#key-features)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Quick Start](#quick-start)
- [Core Concepts](#core-concepts)
- [Usage Examples](#usage-examples)
- [Advanced Features](#advanced-features)
- [Best Practices](#best-practices)
- [Showcase Application](#showcase-application)
- [Contributing](#contributing)

## Overview

Sepia QueryDao addresses common challenges in enterprise database access by providing:

- **Dynamic Query Building**: Define filtering logic once, use any combination of criteria
- **Automatic Relationship Fetching**: Optimize database access and avoid N+1 query problems
- **Multiple Query Services**: Get search, count, exists, and find operations from a single definition
- **Type Safety**: Strongly typed criteria with fluent APIs
- **Performance Optimization**: Automatic query optimization using JPA Entity Graphs

### When to Use QueryDao

QueryDao is particularly valuable when you have:

#### ✅ Dynamic Queries

If you have many criteria to filter data with various combinations, QueryDao eliminates the need to write multiple repository methods. Define the filtering logic once and use any combination of criteria dynamically.

```java
// Instead of writing dozens of methods like:
// findByNameAndCountry(), findByNameAndStadiumCapacity(), etc.
// You write filtering logic once and use it flexibly:
clubDao.search(new ClubCriteria().nameLike("Arsenal").countryIs(england));
clubDao.search(new ClubCriteria().stadiumCapacityGreaterThan(50000));
clubDao.search(new ClubCriteria().nameLike("Arsenal").stadiumCapacityGreaterThan(50000));
```

#### ✅ Dynamic Fetching
When the data you need to fetch varies by use case, QueryDao's Fetching feature automatically handles relationship loading:
```java
// Fetch only basic club info
clubDao.search(criteria);

// Fetch clubs with their stadiums
clubDao.search(criteria.fetch("stadium"));

// Fetch clubs with stadiums and players
clubDao.search(criteria.fetch("stadium", "players"));
```
#### ✅  Multiple Query Operations

Once you define filtering logic, you automatically get multiple query operations:

- search() - Returns list of matching entities
- count() - Returns count of matching records
- exists() - Checks if any records match
- notExist() - Opposite of exists (for readability)
- searchOne() - Returns single optional result
- find() - Finds by primary key

## Key Features

🚀 **Reduced Boilerplate:** No need for multiple repository methods

🔒 **Type Safety:** Strongly typed criteria with compile-time validation

⚡ **Performance:** Automatic query optimization and efficient fetching

🔧 **Maintainable:** Centralized, easy-to-modify query logic

📊 **Consistent:** Standardized patterns across all DAOs

🎯 **Flexible:** Support for complex filtering, sorting, and pagination

## Prerequisites
- **Java 21** or higher
- **Maven 3.6+** or **Gradle 7+**
- **JPA 3.1+** compatible ORM (Hibernate, EclipseLink, etc.)

## Installation

### Maven

````xml
<dependencies>
    <!-- Sepia QueryDao Implementation -->
    <dependency>
        <groupId>fr.groupebpce.sepia.dao</groupId>
        <artifactId>sepia-dao-impl</artifactId>
        <version>1.0.2</version>
    </dependency>
    
    <!-- Spring Boot JPA Starter (if using Spring Boot) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
</dependencies>
````


### Gradle

````yaml
dependencies {
  implementation 'fr.groupebpce.sepia.dao:sepia-dao-impl:1.0.2'
  implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
}
````

## Quick Start
### 1. Create Your Entity
````java
@Entity
@Table(name = "CLUB")
public class Club {
    @Id
    private String code;
    
    @Column(name = "NAME")
    private String name;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_COUNTRY")
    private Country country;
    
    // getters, setters, constructors...
}
````
### 2. Create Your Criteria Class
````java
public class ClubCriteria extends Criteria {
    private StringFilter name;
    private Country country;
    
    // Fluent setters
    public ClubCriteria nameLike(String name) {
        this.name = StringFilter.like(name).ignoreCase();
        return this;
    }
    
    public ClubCriteria countryIs(Country country) {
        this.country = country;
        return this;
    }
    
    // getters...
}
````
### 3. Create Your DAO
````java
@Repository
public class ClubDao extends QueryDao<Club, ClubCriteria> {
    
    public ClubDao() {
        super(Club.class);
    }
    
    @Override
    protected void filter(QueryContext<ClubCriteria> context) {
        ClubCriteria criteria = context.getCriteria();
        
        // Add filtering conditions
        if (criteria.getName() != null) {
            context.addStringCondition("club.name", "clubName", criteria.getName());
        }
        
        if (criteria.getCountry() != null) {
            context.addCondition("club.country = :country")
                   .addParameter("country", criteria.getCountry());
        }
    }
}
````

### 4. Use Your DAO
````java
@Service
public class ClubService {
    
    @Autowired
    private ClubDao clubDao;
    
    public List<Club> searchClubs(String name, Country country) {
        return clubDao.search(new ClubCriteria()
            .nameLike(name)
            .countryIs(country)
            .fetch("country") // Fetch country relationship
            .orderBy(Sorting.asc("name"))
            .paging(new Paging().pageNumber(1).pageSize(10))
        );
    }
    
    public long countClubs(String name) {
        return clubDao.count(new ClubCriteria().nameLike(name));
    }
    
    public boolean clubExists(String name) {
        return clubDao.exists(new ClubCriteria().nameLike(name));
    }
}
````

## Core Concepts
### QueryDao<E, C>
The abstract base class that provides all query operations. Generic types:
- E: **Entity** class (e.g., Club)
- C: **Criteria** class (e.g., ClubCriteria)

### Criteria
Base class for defining search criteria with support for:
- **Filtering**: Custom conditions via filter() method
- **Sorting**: Order results using Sorting objects
- **Pagination**: Limit results using Paging
- **Fetching**: Load relationships using Fetching

### QueryContext
Builds JPQL queries dynamically based on criteria. Provides methods for:
- Adding conditions (**addCondition**())
- Adding parameters (**addParameter**())
- Adding joins (addJoin(), **addLeftJoin**())
- String-specific conditions (**addStringCondition**(), **addLikeCondition**())

## Usage Examples
### Basic Filtering
````java
// Find clubs by name (case-insensitive)
List<Club> clubs = clubDao.search(new ClubCriteria().nameLike("arsenal"));

// Find clubs by exact country
List<Club> frenchClubs = clubDao.search(new ClubCriteria().countryIs(france));

// Combine multiple criteria
List<Club> results = clubDao.search(new ClubCriteria()
    .nameLike("united")
    .countryIs(england)
);
````
### Dynamic Fetching
````java
// Fetch clubs with their stadiums
List<Club> clubsWithStadiums = clubDao.search(new ClubCriteria()
    .fetch("stadium")
);

// Fetch clubs with stadiums and players
List<Club> clubsWithDetails = clubDao.search(new ClubCriteria()
    .fetch("stadium", "players")
);

// Fetch nested relationships
List<Club> clubsWithPlayerCountries = clubDao.search(new ClubCriteria()
    .fetch("players.country")
);
````
### Sorting and Pagination
````java
// Sort by name ascending
List<Club> sortedClubs = clubDao.search(new ClubCriteria()
    .orderBy(Sorting.asc("name"))
);

// Multiple sorting criteria
List<Club> clubs = clubDao.search(new ClubCriteria()
    .orderBy(
        Sorting.asc("country.name"),
        Sorting.desc("name")
    )
);

// Pagination
List<Club> pagedClubs = clubDao.search(new ClubCriteria()
    .paging(new Paging().pageNumber(2).pageSize(20))
);
````
### Query Operations
````java
ClubCriteria criteria = new ClubCriteria().countryIs(spain);

// Get all matching clubs
List<Club> clubs = clubDao.search(criteria);

// Count matching clubs
Long count = clubDao.count(criteria);

// Check if any clubs exist
boolean exists = clubDao.exists(criteria);

// Get single result (if expecting only one)
Optional<Club> club = clubDao.searchOne(criteria);

// Find by primary key
Club realMadrid = clubDao.find("RMA");

// Find by primary key with fetching
Club clubWithPlayers = clubDao.find("RMA", Fetching.fetch("players"));
````
## Advanced Features
### Custom Sorting
Override the sort() method in your DAO for custom sorting logic:
````java
@Override
protected String sort(Sorting sorting) {
    if ("stadiumCapacity".equals(sorting.getProperty())) {
        return getAliasName() + ".stadium.capacity " + sorting.getDirection();
    }
    return super.sort(sorting);
}
````
### Complex Joins
````java
@Override
protected void filter(QueryContext<ClubCriteria> context) {
    ClubCriteria criteria = context.getCriteria();
    
    // Add join for filtering
    if (criteria.getPlayerName() != null) {
        context.addJoin("players", "club.players")
               .addLikeCondition("players.name", "playerName", criteria.getPlayerName());
    }
    
    // Add left join with fetch
    if (criteria.shouldFetchStadium()) {
        context.addLeftJoinFetch("stadium", "club.stadium");
    }
}
````
### String Filtering Options
````java
// Exact match (case-sensitive)
StringFilter.exactMatches("Arsenal")

// Like match (case-insensitive)
StringFilter.like("arsenal").ignoreCase()

// Like match (case-sensitive)
StringFilter.like("Arsenal")

// Custom operator
StringFilter.with("Arsenal", "!=")
````
### Range Filtering
````java
// Bounded range
Range.bounded(1000, 5000)  // between 1000 and 5000

// Minimum only
Range.minorized(1000)      // >= 1000

// Maximum only  
Range.majorized(5000)      // <= 5000
````

## Best Practices
### 1. Use Fluent APIs
````java
// ✅ Good: Fluent and readable
List<Club> clubs = clubDao.search(new ClubCriteria()
    .nameLike("arsenal")
    .countryIs(england)
    .stadiumCapacityGreaterThan(50_000)
    .fetch("stadium", "players")
    .orderBy(Sorting.asc("name"))
    .paging(new Paging().pageNumber(1).pageSize(10))
);

// ❌ Avoid: Verbose setter usage
ClubCriteria criteria = new ClubCriteria();
criteria.setName(StringFilter.like("arsenal"));
criteria.setCountry(england);
// ...
````
### 2. Optimize Fetching
````java
// ✅ Good: Fetch only what you need
clubDao.search(criteria.fetch("country"));  // Only need country

// ❌ Avoid: Over-fetching
clubDao.search(criteria.fetch("country", "stadium", "players", "players.country"));
````
### 3. Use Appropriate Query Methods
````java
// ✅ Good: Use count() for counting
long totalClubs = clubDao.count(criteria);

// ❌ Avoid: Loading all data just to count
long totalClubs = clubDao.search(criteria).size();

// ✅ Good: Use exists() for existence checks
boolean hasClubs = clubDao.exists(criteria);

// ❌ Avoid: Loading data just to check existence
boolean hasClubs = !clubDao.search(criteria).isEmpty();
````
### 4. Handle Null Criteria Gracefully
````java
@Override
protected void filter(QueryContext<ClubCriteria> context) {
    ClubCriteria criteria = context.getCriteria();
    
    // ✅ Good: Check for null before using
    if (criteria.getName() != null) {
        context.addStringCondition("club.name", "clubName", criteria.getName());
    }
    
    if (StringUtils.isNotBlank(criteria.getStadiumName())) {
        context.addLikeCondition("club.stadium.name", "stadiumName", criteria.getStadiumName());
    }
}
````
### 5. Use Meaningful Parameter Names
````java
// ✅ Good: Clear parameter names
context.addCondition("club.stadium.capacity >= :minCapacity")
       .addParameter("minCapacity", criteria.getMinCapacity());

// ❌ Avoid: Generic parameter names
context.addCondition("club.stadium.capacity >= :param1")
       .addParameter("param1", criteria.getMinCapacity());
````

## Showcase Application
The repository includes a complete showcase application (showcase-dao/) demonstrating:

- **Domain Model**: Football/soccer entities (Club, Player, Country, Stadium)
- **Complete DAOs**: Examples of QueryDao implementations
- **Comprehensive Tests**: Various usage patterns and scenarios
- **Spring Boot Integration**: Full application setup

The tests demonstrate:

- Basic and complex filtering
- Dynamic fetching strategies
- Sorting and pagination
- Performance optimization techniques
- Error handling patterns

## Contributing
We welcome contributions! Please:

- Fork the repository
- Create a feature branch (git checkout -b feature/amazing-feature)
- Commit your changes (git commit -m 'Add amazing feature')
- Push to the branch (git push origin feature/amazing-feature)
- Open a Pull Request
