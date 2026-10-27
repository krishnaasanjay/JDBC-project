CREATE DATABASE IF NOT EXISTS employee_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE employee_db;

CREATE TABLE IF NOT EXISTS employee (
    emp_id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    department VARCHAR(50) NOT NULL,
    salary DECIMAL(10,2) NOT NULL
);

INSERT IGNORE INTO employee (emp_id, name, department, salary) VALUES
(101, 'Aarav Sharma', 'IT', 65000.00),
(102, 'Priya Nair', 'HR', 48000.00),
(103, 'Rahul Verma', 'Finance', 55000.00),
(104, 'Sneha Iyer', 'IT', 72000.00),
(105, 'Karthik Raj', 'Marketing', 51000.00),
(106, 'Meena Kumari', 'HR', 46000.00),
(107, 'Arjun Reddy', 'Finance', 60000.00),
(108, 'Divya Menon', 'Marketing', 49000.00),
(109, 'Vikram Singh', 'IT', 85000.00),
(110, 'Anjali Gupta', 'Sales', 42000.00),
(111, 'Suresh Babu', 'Sales', 45000.00),
(112, 'Lakshmi Narayanan', 'Finance', 68000.00);
