DELETE FROM City
WHERE city_id=${city_id} returning *;