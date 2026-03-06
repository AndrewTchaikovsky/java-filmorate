# java-filmorate
Template repository for Filmorate project.
## Database Schema
[![Filmorate DB](docs/java-filmorate-db.png)](docs/java-filmorate-db.png)
### Get Top Popular Films

```sql
SELECT f.id,
       f.name,
       COUNT(l.user_id) AS likes
FROM films f
LEFT JOIN likes l ON f.id = l.film_id
GROUP BY f.id
ORDER BY likes DESC
LIMIT 10;
```
