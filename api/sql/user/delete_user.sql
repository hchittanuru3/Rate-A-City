DELETE FROM App_User
WHERE email=${email} returning *;