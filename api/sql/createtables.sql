CREATE TABLE IF NOT EXISTS Category (
    category_name varchar(50) NOT NULL,
    PRIMARY KEY (category_name)
);

CREATE TABLE IF NOT EXISTS App_User (
    email varchar(50) NOT NULL,
    password int NOT NULL,
    date_joined timestamp DEFAULT CURRENT_TIMESTAMP,
    is_manager boolean NOT NULL,
    is_suspended boolean,
    PRIMARY KEY (email)
);

CREATE TABLE IF NOT EXISTS Reviewable_Entity (
    entity_id int NOT NULL,
    is_Pending boolean NOT NULL,
    is_City boolean NOT NULL ,
    submitter_email varchar(50),
    date_submitted timestamp DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (entity_id),
    FOREIGN KEY (submitter_email) REFERENCES App_User (email) ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS Review (
    entity_id int NOT NULL,
    author_email varchar(50) NOT NULL,
    comment text,
    rating int NOT NULL,
    date_created timestamp DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (entity_id) REFERENCES Reviewable_Entity (entity_id) ON DELETE CASCADE,
    FOREIGN KEY (author_email) REFERENCES App_User (email) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT PK_Review PRIMARY KEY (entity_id, author_email),
    CHECK (rating >= 1 AND rating <=5)
);

CREATE TABLE IF NOT EXISTS City (
    city_id int NOT NULL,
    name varchar(30) NOT NULL,
    country varchar (30) NOT NULL,
    state varchar (30),
    PRIMARY KEY (city_id),
    FOREIGN KEY (city_id) REFERENCES Reviewable_Entity (entity_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Attraction (
    street_address varchar(50) NOT NULL,
    name varchar(25) NOT NULL,
    description text NOT NULL,
    attraction_id int NOT NULL,
    city_id int NOT NULL,
    PRIMARY KEY (attraction_id),
    FOREIGN KEY (city_id) REFERENCES City (city_id) ON DELETE CASCADE,
    FOREIGN KEY (attraction_id) REFERENCES Reviewable_Entity(entity_id) ON DELETE CASCADE,
    CONSTRAINT U_Attraction UNIQUE (name, street_address)
);

CREATE TABLE IF NOT EXISTS Attraction_Category_List (
    c_Name varchar(25) NOT NULL,
    attraction_id int NOT NULL,
    FOREIGN KEY (c_Name) REFERENCES Category (category_name) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (attraction_id) REFERENCES Attraction (attraction_id) ON DELETE CASCADE,
    CONSTRAINT Pk_Attraction_Category PRIMARY KEY (c_Name, attraction_id)
);

CREATE TABLE IF NOT EXISTS Contact_Info (
    attraction_id int NOT NULL,
    info varchar(100) NOT NULL,
    CONSTRAINT Pk_Contact_Info PRIMARY KEY (attraction_id, info),
    FOREIGN KEY (attraction_id) REFERENCES Attraction (attraction_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Hours_Of_Operation (
    attraction_id int NOT NULL,
    hours varchar(100) NOT NULL,
    FOREIGN KEY (attraction_id) REFERENCES Attraction (attraction_id) ON DELETE CASCADE,
    CONSTRAINT Pk_Hours PRIMARY KEY (attraction_id, hours)
);
