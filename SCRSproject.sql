-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Oct 08, 2025 at 12:31 AM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `SCRSproject`
--

-- --------------------------------------------------------

--
-- Table structure for table `Courses`
--

CREATE TABLE `Courses` (
  `id` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `credits` int(11) NOT NULL,
  `department` varchar(100) NOT NULL,
  `capacity` int(11) NOT NULL DEFAULT 5,
  `ADDDROPDEADLINE` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `Courses`
--

INSERT INTO `Courses` (`id`, `title`, `credits`, `department`, `capacity`, `ADDDROPDEADLINE`) VALUES
(1, 'Designing', 2, 'CS', 5, '2025-10-07'),
(5, 'Marketing', 3, 'Counting', 5, '2025-10-03'),
(6, 'Laravel', 2, 'SD', 5, '2025-10-03'),
(9, 'java language', 2, 'IT', 5, '2025-10-05'),
(14, 'Backend course', 3, 'IT', 5, '2025-10-06'),
(16, 'Freelancing', 4, 'Computing', 5, '2025-10-07'),
(17, 'javaFx', 4, 'CS', 5, '2025-10-05'),
(18, 'Frontend course', 3, 'SD', 5, '2025-10-07');

-- --------------------------------------------------------

--
-- Table structure for table `Enrollments`
--

CREATE TABLE `Enrollments` (
  `id` int(11) NOT NULL,
  `student_id` int(11) NOT NULL,
  `course_id` int(11) NOT NULL,
  `enrollment_date` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `Enrollments`
--

INSERT INTO `Enrollments` (`id`, `student_id`, `course_id`, `enrollment_date`) VALUES
(1, 2, 5, '2025-09-04'),
(2, 1, 1, '2025-09-13'),
(14, 27, 1, '2025-09-10'),
(17, 31, 1, '2025-09-01'),
(21, 32, 16, '2025-10-01'),
(22, 4, 16, '2025-10-02'),
(23, 33, 9, '2025-09-17'),
(24, 3, 14, '2025-10-01'),
(27, 1, 16, '2025-09-24'),
(28, 34, 18, '2025-08-12'),
(29, 2, 16, '2025-10-03'),
(30, 3, 16, '2025-09-30'),
(31, 33, 1, '2025-10-02'),
(32, 7, 1, '2025-09-30'),
(33, 1, 18, '2025-10-03'),
(34, 33, 14, '2025-10-01');

-- --------------------------------------------------------

--
-- Table structure for table `Students`
--

CREATE TABLE `Students` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `major` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `Students`
--

INSERT INTO `Students` (`id`, `name`, `major`) VALUES
(2, 'Adam Abed', 'Counting'),
(1, 'Ahlam Abu Diab', 'SD'),
(3, 'Ahmed Madi', 'Multimedia'),
(32, 'Alia Wael', 'Multimedia'),
(27, 'Basema Faraj', 'SD'),
(31, 'Huda Sameer', 'IT'),
(34, 'Mohammed  Diab', 'Multimedia'),
(4, 'Nada Sameer', 'CS'),
(7, 'Rami Hadi', 'CS'),
(33, 'Waleed Rabah', 'IT');

-- --------------------------------------------------------

--
-- Table structure for table `Users`
--

CREATE TABLE `Users` (
  `id` int(11) NOT NULL,
  `first_name` varchar(100) NOT NULL,
  `last_name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password_hash` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `Users`
--

INSERT INTO `Users` (`id`, `first_name`, `last_name`, `email`, `password_hash`) VALUES
(5, 'Ahlam', 'Abu Diab', 'ahlamdyab2020@gmail.com', '827ccb0eea8a706c4c34a16891f84e7b'),
(6, 'Adam', 'Abed', 'adamabed1996@gmail.com', '827ccb0eea8a706c4c34a16891f84e7b'),
(7, 'Hala', 'Madi', 'hala@gmail.com', '5b203658f1b0da1596db00ba59ee753f'),
(8, 'Ali', 'Ahmed', 'ali@gmail.com', '86318e52f5ed4801abe1d13d509443de'),
(9, 'Omar', 'Dali', 'omar@gmail.com', 'd4466cce49457cfea18222f5a7cd3573'),
(10, 'Huda', 'Saleem', 'huda@gmail.com', '0075a4e7a2e71083262da135ecdbdd14'),
(11, 'Wael', 'Ahmed', 'wael@gmail.com', '39057f4a925f064130b3b7059a0e3294'),
(13, 'Alia', 'Ahmed', 'alia@gmail.com', '86c8c6c90abd00c209e39736da1ec1fd'),
(14, 'Areej', 'Dali', 'areej@gmail.com', '0c0c48aaf0d7497059428dbe2b488862'),
(15, 'Dalia', 'Fathi', 'dalia@gmail.com', '5e6e3d387a8e029ba1a6176684d524bd'),
(16, 'Aseel', 'Khaled', 'aseel@gmail.com', '5e32ec82a0d3d461e8e2ea686e57adaf');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `Courses`
--
ALTER TABLE `Courses`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uniq_title_department` (`title`,`department`);

--
-- Indexes for table `Enrollments`
--
ALTER TABLE `Enrollments`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uq_student_course` (`student_id`,`course_id`),
  ADD UNIQUE KEY `UNIQUE_student_course` (`student_id`,`course_id`),
  ADD KEY `course_id` (`course_id`);

--
-- Indexes for table `Students`
--
ALTER TABLE `Students`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uniq_name_major` (`name`,`major`);

--
-- Indexes for table `Users`
--
ALTER TABLE `Users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `Courses`
--
ALTER TABLE `Courses`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT for table `Enrollments`
--
ALTER TABLE `Enrollments`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=35;

--
-- AUTO_INCREMENT for table `Students`
--
ALTER TABLE `Students`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=35;

--
-- AUTO_INCREMENT for table `Users`
--
ALTER TABLE `Users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `Enrollments`
--
ALTER TABLE `Enrollments`
  ADD CONSTRAINT `enrollments_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `Students` (`id`),
  ADD CONSTRAINT `enrollments_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `Courses` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
