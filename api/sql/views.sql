#AttractionsList
CREATE OR REPLACE VIEW one AS SELECT attraction_id, group_concat(c_Name) as Categories, Attraction.name, City.name as Location
FROM Attraction_Category_List NATURAL JOIN Attraction JOIN Reviewable_Entity ON attraction_id=entity_id JOIN City on Attraction.city_id=City.city_id
WHERE is_Pending=0
GROUP BY attraction_id;

CREATE OR REPLACE VIEW two AS SELECT entity_id, COUNT(*) as numReviews, AVG(rating) as avgRating
FROM Reviewable_Entity NATURAL JOIN Review
WHERE is_Pending=0
GROUP BY entity_id;

SELECT * FROM one JOIN two ON one.attraction_id=two.entity_id;

#UserReviews
CREATE OR REPLACE VIEW userReviews AS SELECT rating, comment, entity_id, entity.name
FROM Review JOIN (SELECT city_id as id, name FROM City UNION SELECT attraction_id as id, name FROM Attraction) as entity ON Review.entity_id=entity.id
WHERE author_email = SessionInfo.getInstance().getEmail()

SELECT * FROM userReviews

#CityController
CREATE OR REPLACE VIEW attr1 AS SELECT attraction_id, group_concat(c_Name) as Categories, Attraction.name, street_address
FROM Attraction_Category_List NATURAL JOIN Attraction JOIN Reviewable_Entity ON attraction_id=entity_id
WHERE is_Pending=0 AND Attraction.city_id=SessionInfo.getInstance().getCityIdStorage()
GROUP BY attraction_id;

CREATE OR REPLACE VIEW attr2 AS SELECT entity_id, COUNT(*) as numReviews, AVG(rating) as avgRating
FROM Reviewable_Entity NATURAL JOIN Review
WHERE is_Pending=0
GROUP BY entity_id;

SELECT * FROM attr1 JOIN attr2 ON attr1.attraction_id=attr2.entity_id;

#UsersListController
CREATE OR REPLACE VIEW users AS SELECT email, date_joined, is_manager, is_suspended
FROM App_User;
SELECT * FROM users;

#PendingCitiesController
CREATE OR REPLACE VIEW pendingCities AS SELECT name, country, submitter_email, rating, comment
FROM City JOIN Review ON city_id=Review.entity_id JOIN Reviewable_Entity ON city_id=Reviewable_Entity.entity_id
WHERE is_Pending=1;

SELECT DISTINCT * FROM pendingCities;

#CategoriesListController
CREATE OR REPLACE VIEW one AS SELECT c_Name, COUNT(attraction_id)
FROM Attraction_Category_List JOIN Reviewable_Entity ON attraction_id=entity_id
WHERE is_Pending=0
GROUP BY c_Name;

SELECT * FROM one;

#PendingAttractionsController
CREATE OR REPLACE VIEW one AS SELECT Attraction.attraction_id, Attraction.name, group_concat(c_Name) as Categories, City.name as city, description,  submitter_email, group_concat(info) as contact_info, group_concat(hours) as hours_of_operation
FROM Attraction_Category_List NATURAL JOIN Attraction JOIN Reviewable_Entity ON attraction_id=entity_id JOIN City on Attraction.city_id=City.city_id LEFT JOIN Hours_of_Operation ON Attraction.attraction_id=Hours_Of_Operation.attraction_id LEFT JOIN Contact_Info ON Attraction.attraction_id=Contact_Info.attraction_id
WHERE is_Pending=1
GROUP BY attraction_id;

CREATE OR REPLACE VIEW two AS SELECT group_concat(rating) as rating, group_concat(comment) as comment, entity_id
FROM Review
GROUP BY entity_id;

SELECT * FROM one JOIN two ON one.attraction_id=two.entity_id;

#AttractionReviewsController
CREATE OR REPLACE VIEW attractionReviews AS SELECT author_email, rating, comment
FROM Review
WHERE entity_id = SessionInfo.getInstance().getAttractionIdStorage();

SELECT * FROM attractionReviews;

#CityReviewsController
CREATE OR REPLACE VIEW cityReviews AS SELECT author_email, rating, comment
FROM Review
WHERE entity_id = SessionInfo.getInstance().getCityIdStorage();

SELECT * FROM cityReviews;


