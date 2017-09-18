DELETE FROM Attraction
WHERE attraction_id=${attraction_id} returning *;