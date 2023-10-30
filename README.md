# 2023-fall-cs122b-team-winner

Fabflix URL: http://13.58.98.182:8080/fabflix/

## Project 2
Project 2 Demo Video URL: 

Brian Le (bale4): Task 2 Implement the Main Page (with Search and Browse) and Task 3 Extend Project 1

Henry Reyes (henreyes): Task 1 Implement the Login Page and Task 4 Implement the Shopping Cart

### Substring matching:
Line 137 in src/MoviesServlet is the search query. Below is the Where statement of SQL that searches

    WHERE m.id is not null" + movie_name_cond + director_cond + year_cond + star_name_cond

The variables are set in lines 77-88 of src/MoviesServlet if their parameters are passed in. 

If movie name (a), director (b), year (c), and star (d) are all defined, then the Where condition will be

    WHERE m.id is not null and m.title LIKE '%a%' and m.director LIKE '%b%' and m.year = c and s.name = '%d%'

Below is the nested query that returns the ids based on the search 

"SELECT DISTINCT m.id as id, r.rating as rating, count(*) OVER() AS total_rows\n" +

"FROM movies m\n" +

"LEFT JOIN ratings r ON r.movieId = m.id\n" +

"LEFT JOIN stars_in_movies sm ON sm.movieId = m.id\n" +

"LEFT JOIN stars s ON sm.starId = s.id\n" +

"WHERE m.id is not null" + movie_name_cond + director_cond + year_cond + star_name_cond + "\n" +

"GROUP BY m.id\n" +

"ORDER BY " + firstOrder + "\n" +

"LIMIT ? OFFSET ?"


## Project 1
Project 1 Demo Video URL: https://youtu.be/f_cJnsWaG3c

Brian Le (bale4): Wrote MySQL table schema, Created Single Movie List page and Home Page, Styling tables, and Deployed on AWS

Henry Reyes (henreyes): Wrote MySQL queries for Movie list and Single Movie, java servlets, and javascript 



## Special Instructions: 

Change Username/Password in context.xml to match user with access to moviedb database


Instead of "cp ./target/*.war /var/lib/tomcat10/webapps/", used "sudo cp ./target/*.war /var/lib/tomcat10/webapps/"
