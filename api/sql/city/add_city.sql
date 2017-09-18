INSERT INTO City
  (
    city_id,
    country,
    state,
    entity_id

  )
VALUES
  (
    ${city_id},
    ${country},
    ${state},
    ${entity_id}
  )
RETURNING *;