INSERT INTO App_User
  (
    email,
    password,
    is_manager,
    is_suspended
  )
VALUES
  (
    ${email},
    ${password},
    ${is_manager},
    ${is_suspended}
  )
RETURNING *;