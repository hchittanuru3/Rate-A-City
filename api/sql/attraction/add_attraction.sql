INSERT INTO App_User
  (
    street_address,
    name,
    description,
    attraction_id,
    city_id
  )
VALUES
  (
    ${street_address},
    ${name},
    ${description},
    ${attraction_id},
    ${city_id}
  )
RETURNING *;