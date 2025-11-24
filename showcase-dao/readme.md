# QueryDao

## Presentation

### Motivation

The code responsible for the relationship with the database can take a lot of time to write, represent an important number of rows, and be repetitive.
Especially when you want to apply the best practices with an ORM such as JPA, in order to get good performance.
Spring Data proposes a very handy way to query the database, through the Derivative Query Methods.
With very few code, we can achieve to query the data in various way.   

However, there are some cases where this can become complicated to use this approach.
This is where Sepia's QueryDao can be interesting.

### Good candidates for QueryDao

#### Dynamic queries

If you have a lot of criteria to filter the data, and various combinations of them, you will have to adapt your queries depending on the criteria that are filled.
With the QueryDao, you only need to indicate the code once how to filter the data depending on the presence or not of each criterium.
Once you've done that, you can invoke the `QueryDao.search()` method with any combination of criteria. The query generated will take in consideration only the criteria specified.

#### Dynamic fetching

In parallel of filtering, the data that we need to fetch from the database may vary depending on the use cases, and then, can represent a lot of different combinations in itself too.
To handle that with minimum rows of code, a part of the Criteria is dedicated to the Fetching.
The implementation of each `Fecthing` will contain different attributes to indicate the QueryDao which property to fetch.
Again, for each potential data to fetch, you only need once to precise the QueryDao how to load these data.

#### Services for free

One thing that also helps accelerate the development of the code in charge of the queries is the fact that once you defined how to filter the data, you not only get the `search()` method.
You also have :
- `count()` : returns the number of rows matching the criteria
- `exists()` : returns a boolean indicating if there is any row matching the criteria
- `notExist()` : returns the opposite of `exists()` (for readability purpose, avoids usage of `!`)
- `searchOne()` : returns the (supposedly) only single row matching the criteria (if exists) as an `Optional` (instead of a `List` if invoking `search()`)
The QueryDao is able to provide all these services since the global query is predictable in each case, and the only thing
