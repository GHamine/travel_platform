
DROP DATABASE IF EXISTS travel_agency;
CREATE DATABASE travel_agency;

USE travel_agency;



CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('admin', 'client') DEFAULT 'client' NOT NULL,
    budget DECIMAL(10, 2) NOT NULL,
    preferred_destination VARCHAR(100) NOT NULL,
    travel_duration INT NOT NULL
);


CREATE TABLE travel_packages (
    package_id INT AUTO_INCREMENT PRIMARY KEY,
    package_name VARCHAR(100),
    destination VARCHAR(100),
    total_price DECIMAL(10, 2),
    travel_dates int,
    description TEXT
);


CREATE TABLE flights (
    flight_id INT AUTO_INCREMENT PRIMARY KEY,
    airline_name VARCHAR(100),
    flight_number VARCHAR(50),
    departure_city VARCHAR(100),
    destination_city VARCHAR(100),
    departure_date DATETIME,
    arrival_date DATETIME,
    price DECIMAL(10, 2)
);

CREATE TABLE hotels (
    hotel_id INT AUTO_INCREMENT PRIMARY KEY,
    hotel_name VARCHAR(100),
    check_in_date DATETIME,
    check_out_date DATETIME,
    room_type VARCHAR(50),
    number_of_guests INT,
    price DECIMAL(10, 2)
);

CREATE TABLE activities (
    activity_id INT AUTO_INCREMENT PRIMARY KEY,
    activity_name VARCHAR(100),
    activity_date DATETIME,
    location VARCHAR(100),
    duration INT, -- in minutes or hours
    price DECIMAL(10, 2)
);


CREATE TABLE bookings (
    booking_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    package_id INT,
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    booking_status ENUM('Pending Payment', 'Confirmed', 'Cancelled') DEFAULT 'Pending Payment',
    booking_type ENUM('flight', 'hotel', 'activity') NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (package_id) REFERENCES travel_packages(package_id),

    flight_id INT,
    hotel_id INT,
    activity_id INT,
    FOREIGN KEY (flight_id) REFERENCES flights(flight_id),
    FOREIGN KEY (hotel_id) REFERENCES hotels(hotel_id),
    FOREIGN KEY (activity_id) REFERENCES activities(activity_id)
);


CREATE TABLE itineraries (
    itinerary_id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id INT,
    details TEXT,
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id)
);



INSERT INTO users (username, password, role, budget, preferred_destination, travel_duration) VALUES 
('amine', '0000', 'admin', 1000.00, 'Paris', 7),
('gherbi', '1234', 'client', 5500.00, 'Paris', 7);


-- Insert dummy data into users table
INSERT INTO users (username, password, role, budget, preferred_destination, travel_duration) VALUES
('john_doe', 'password123', 'client', 1500.00, 'Paris', 7),
('jane_smith', 'password456', 'client', 2000.00, 'New York', 5),
('alice_wonder', 'password789', 'client', 3000.00, 'Tokyo', 10),
('bob_builder', 'password101', 'client', 2500.00, 'London', 8),
('charlie_brown', 'password202', 'client', 1800.00, 'Rome', 6),
('david_smith', 'password303', 'client', 2200.00, 'Berlin', 7),
('emily_clark', 'password404', 'client', 2700.00, 'Sydney', 9),
('frank_underwood', 'password505', 'client', 3200.00, 'Dubai', 6),
('george_martin', 'password606', 'client', 1900.00, 'Barcelona', 5),
('hannah_baker', 'password707', 'client', 2100.00, 'Amsterdam', 8);



-- Insert dummy data into travel_packages table
INSERT INTO travel_packages (package_name, destination, total_price, travel_dates, description) VALUES
('Paris Getaway', 'Paris', 1200.00, 7, 'A week-long trip to Paris including flights and hotel.'),
('New York Adventure', 'New York', 1800.00, 5, 'Explore the city that never sleeps with this 5-day package.'),
('Tokyo Experience', 'Tokyo', 2500.00, 7, 'Experience the culture and excitement of Tokyo with this 7-day package.'),
('London Explorer', 'London', 2200.00, 7, 'Discover the historic and modern attractions of London.'),
('Rome Discovery', 'Rome', 1600.00, 6, 'Enjoy the ancient wonders and culinary delights of Rome.'),
('Berlin Highlights', 'Berlin', 1900.00, 6, 'Experience the vibrant culture and history of Berlin.'),
('Sydney Adventure', 'Sydney', 2700.00, 7, 'Explore the stunning beaches and landmarks of Sydney.'),
('Dubai Luxury', 'Dubai', 3200.00, 7, 'Indulge in the luxury and modernity of Dubai.'),
('Barcelona Fiesta', 'Barcelona', 3200.00, 9, 'Enjoy the lively atmosphere and beautiful architecture of Barcelona.'),
('Amsterdam Journey', 'Amsterdam', 2300.00, 7, 'Discover the canals and museums of Amsterdam.');


-- Insert dummy data into flights
INSERT INTO flights (airline_name, flight_number, departure_city, destination_city, departure_date, arrival_date, price)
VALUES
('Skyline Airways', 'SK123', 'New York', 'Los Angeles', '2025-01-05 08:00:00', '2025-01-05 11:00:00', 200.00),
('Midwest Wings', 'MW456', 'Chicago', 'Miami', '2025-01-10 09:30:00', '2025-01-10 13:00:00', 150.00),
('Pacific Horizon', 'PH789', 'San Francisco', 'Seattle', '2025-01-15 12:00:00', '2025-01-15 14:00:00', 100.00),
('Atlantic Breeze', 'AB101', 'Boston', 'Denver', '2025-01-18 07:00:00', '2025-01-18 09:30:00', 250.00),
('Southern Trails', 'ST234', 'Houston', 'Orlando', '2025-01-20 10:00:00', '2025-01-20 13:30:00', 180.00),
('Cityline Express', 'CE345', 'Philadelphia', 'San Diego', '2025-01-22 09:00:00', '2025-01-22 12:30:00', 300.00),
('Mountain Air', 'MA456', 'Phoenix', 'Portland', '2025-01-25 14:00:00', '2025-01-25 16:00:00', 120.00),
('Rustic Flyers', 'RF567', 'Detroit', 'Charlotte', '2025-01-27 08:30:00', '2025-01-27 11:00:00', 140.00),
('Music City Airlines', 'MCA678', 'Nashville', 'Las Vegas', '2025-01-30 07:00:00', '2025-01-30 10:00:00', 220.00),
('Peach State Jets', 'PSJ789', 'Atlanta', 'Dallas', '2025-02-01 09:00:00', '2025-02-01 11:30:00', 170.00);

-- Insert dummy data into hotels
INSERT INTO hotels (hotel_name, check_in_date, check_out_date, room_type, number_of_guests, price)
VALUES
('The Grand NYC', '2025-01-05 15:00:00', '2025-01-10 12:00:00', 'Deluxe', 2, 500.00),
('Sunshine Suites', '2025-01-11 14:00:00', '2025-01-15 12:00:00', 'Suite', 4, 1000.00),
('Urban Haven', '2025-01-16 16:00:00', '2025-01-20 11:00:00', 'Standard', 1, 300.00),
('Highland Retreat', '2025-01-21 13:00:00', '2025-01-25 12:00:00', 'Luxury', 3, 1200.00),
('Family Inn', '2025-01-26 14:00:00', '2025-01-30 10:00:00', 'Family', 5, 800.00),
('Boutique Bliss', '2025-02-01 15:00:00', '2025-02-05 12:00:00', 'Boutique', 2, 700.00),
('Cozy Economy', '2025-02-06 14:00:00', '2025-02-10 11:00:00', 'Economy', 2, 400.00),
('Prestige Stay', '2025-02-11 13:00:00', '2025-02-15 10:00:00', 'Premium', 3, 900.00),
('Vista Lodge', '2025-02-16 16:00:00', '2025-02-20 12:00:00', 'Standard', 4, 600.00),
('Opulent Oasis', '2025-02-21 15:00:00', '2025-02-25 12:00:00', 'Luxury', 5, 1100.00),
('Serene Stay', '2025-02-26 14:00:00', '2025-03-02 10:00:00', 'Boutique', 3, 800.00);

-- Insert dummy data into activities
INSERT INTO activities (activity_name, activity_date, location, duration, price)
VALUES
('City Tour', '2025-01-06 09:00:00', 'Los Angeles', 240, 50.00),
('Museum Visit', '2025-01-12 10:00:00', 'Miami', 180, 30.00),
('Hiking Trip', '2025-01-17 07:00:00', 'Seattle', 300, 60.00),
('Boat Ride', '2025-01-22 14:00:00', 'Orlando', 120, 80.00),
('Concert', '2025-01-27 20:00:00', 'Las Vegas', 180, 120.00),
('Food Tour', '2025-02-02 11:00:00', 'Dallas', 210, 70.00),
('Scuba Diving', '2025-02-07 10:00:00', 'Hawaii', 300, 200.00),
('Skydiving', '2025-02-14 09:00:00', 'Arizona', 60, 250.00),
('Cooking Class', '2025-02-21 11:00:00', 'New York', 180, 100.00),
('Photography Tour', '2025-02-28 08:00:00', 'Yellowstone', 360, 150.00),
('Downtown Discovery', '2025-01-06 09:00:00', 'Los Angeles', 240, 50.00),
('Art Deco Adventure', '2025-01-12 10:00:00', 'Miami', 180, 30.00),
('Mountain Trek', '2025-01-17 07:00:00', 'Seattle', 300, 60.00),
('Lakeside Cruise', '2025-01-22 14:00:00', 'Orlando', 120, 80.00),
('Live Music Gala', '2025-01-27 20:00:00', 'Las Vegas', 180, 120.00),
('Taste of Texas', '2025-02-02 11:00:00', 'Dallas', 210, 70.00),
('Coral Adventure', '2025-02-07 10:00:00', 'Hawaii', 300, 200.00),
('Desert Heights', '2025-02-14 09:00:00', 'Arizona', 60, 250.00),
('Chef s Table', '2025-02-21 11:00:00', 'New York', 180, 100.00),
('Wildlife Wonders', '2025-02-28 08:00:00', 'Yellowstone', 360, 150.00);





-- Insert dummy data into bookings table

INSERT INTO bookings (user_id, package_id, booking_status, booking_type, flight_id, hotel_id, activity_id)
VALUES
(1, 1, 'Confirmed', 'flight', 1, NULL, NULL),
(2, 1, 'Confirmed', 'hotel', NULL, 1, NULL),
(3, 1, 'Confirmed', 'activity', NULL, NULL, 1),
(4, 2, 'Confirmed', 'flight', 2, NULL, NULL),
(5, 2, 'Confirmed', 'hotel', NULL, 2, NULL),
(6, 2, 'Confirmed', 'activity', NULL, NULL, 2),
(7, 3, 'Confirmed', 'flight', 3, NULL, NULL),
(8, 3, 'Confirmed', 'hotel', NULL, 3, NULL),
(9, 3, 'Confirmed', 'activity', NULL, NULL, 3),
(1, 4, 'Confirmed', 'flight', 4, NULL, NULL),
(2, 4, 'Confirmed', 'hotel', NULL, 4, NULL),
(3, 4, 'Confirmed', 'activity', NULL, NULL, 4),
(4, 5, 'Confirmed', 'flight', 5, NULL, NULL),
(5, 5, 'Confirmed', 'hotel', NULL, 5, NULL),
(6, 6, 'Confirmed', 'activity', NULL, NULL, 5),
(7, 7, 'Confirmed', 'flight', 6, NULL, NULL),
(8, 7, 'Confirmed', 'hotel', NULL, 6, NULL),
(9, 7, 'Confirmed', 'activity', NULL, NULL, 6),
(1, 8, 'Confirmed', 'flight', 7, NULL, NULL),
(1, 8, 'Confirmed', 'hotel', NULL, 7, NULL);


-- Insert dummy data into itineraries table
INSERT INTO itineraries (booking_id, details) VALUES
(1, 'Day 1: Arrival in Paris\nDay 2: Eiffel Tower visit\nDay 3: Louvre Museum\nDay 4: Seine River Cruise\nDay 5: Free day\nDay 6: Shopping\nDay 7: Departure'),
(2, 'Day 1: Arrival in New York\nDay 2: Statue of Liberty\nDay 3: Central Park\nDay 4: Broadway Show\nDay 5: Departure'),
(3, 'Day 1: Arrival in Tokyo\nDay 2: Tokyo Tower\nDay 3: Shibuya Crossing\nDay 4: Meiji Shrine\nDay 5: Tsukiji Fish Market\nDay 6: Akihabara\nDay 7: Departure'),
(4, 'Day 1: Arrival in London\nDay 2: Buckingham Palace\nDay 3: Tower of London\nDay 4: British Museum\nDay 5: West End Show\nDay 6: Free day\nDay 7: Departure'),
(5, 'Day 1: Arrival in Rome\nDay 2: Colosseum\nDay 3: Vatican City\nDay 4: Pantheon\nDay 5: Trevi Fountain\nDay 6: Roman Forum\nDay 7: Departure'),
(6, 'Day 1: Arrival in Berlin\nDay 2: Brandenburg Gate\nDay 3: Berlin Wall\nDay 4: Museum Island\nDay 5: Checkpoint Charlie\nDay 6: Free day\nDay 7: Departure'),
(7, 'Day 1: Arrival in Sydney\nDay 2: Sydney Opera House\nDay 3: Bondi Beach\nDay 4: Sydney Harbour Bridge\nDay 5: Taronga Zoo\nDay 6: Blue Mountains\nDay 7: Departure'),
(8, 'Day 1: Arrival in Dubai\nDay 2: Burj Khalifa\nDay 3: Dubai Mall\nDay 4: Desert Safari\nDay 5: Palm Jumeirah\nDay 6: Dubai Marina\nDay 7: Departure'),
(9, 'Day 1: Arrival in Barcelona\nDay 2: Sagrada Familia\nDay 3: Park Güell\nDay 4: La Rambla\nDay 5: Gothic Quarter\nDay 6: Beach day\nDay 7: Departure'),
(10, 'Day 1: Arrival in Amsterdam\nDay 2: Canal Cruise\nDay 3: Van Gogh Museum\nDay 4: Anne Frank House\nDay 5: Rijksmuseum\nDay 6: Vondelpark\nDay 7: Departure');

