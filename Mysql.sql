create database Library_Management ;

use Library_Management;

CREATE TABLE users (
  id INT PRIMARY KEY AUTO_INCREMENT COMMENT '读者ID',
  username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  password VARCHAR(255) NOT NULL COMMENT '密码（bcrypt哈希）',
  email VARCHAR(100) COMMENT '邮箱',
  phone VARCHAR(20) COMMENT '手机号',
  real_name VARCHAR(50) COMMENT '真实姓名',
  register_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  role ENUM('reader','admin') DEFAULT 'reader' COMMENT '角色',
  status TINYINT DEFAULT 1 COMMENT '1=正常 0=禁用',
  max_borrow_count INT DEFAULT 5 COMMENT '最大可借数量',
  KEY idx_role (role),
  KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='读者表';

CREATE TABLE book_categories (
  id INT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
  name VARCHAR(50) NOT NULL COMMENT '分类名称',
  description VARCHAR(200) COMMENT '分类描述'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书分类表';

CREATE TABLE books (
  id INT PRIMARY KEY AUTO_INCREMENT COMMENT '图书ID',
  title VARCHAR(200) NOT NULL COMMENT '书名',
  author VARCHAR(100) COMMENT '作者',
  isbn VARCHAR(20) COMMENT 'ISBN编号',
  publisher VARCHAR(100) COMMENT '出版社',
  publish_year INT COMMENT '出版年份',
  category_id INT COMMENT '分类ID',
  total_copies INT NOT NULL DEFAULT 1 COMMENT '总册数',
  available_copies INT NOT NULL DEFAULT 1 COMMENT '可借册数',
  location VARCHAR(50) COMMENT '书架位置',
  cover_url VARCHAR(300) COMMENT '封面图片URL',
  description TEXT COMMENT '图书简介',
  status TINYINT DEFAULT 1 COMMENT '1=在架 0=下架',
  KEY idx_category (category_id),
  KEY idx_available (available_copies),
  KEY idx_isbn (isbn),
  FOREIGN KEY (category_id) REFERENCES book_categories(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书表';

CREATE TABLE borrow_records (
  id INT PRIMARY KEY AUTO_INCREMENT COMMENT '借阅ID',
  user_id INT NOT NULL COMMENT '读者ID',
  book_id INT NOT NULL COMMENT '图书ID',
  borrow_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '借出日期',
  due_date DATETIME NOT NULL COMMENT '应还日期',
  return_date DATETIME DEFAULT NULL COMMENT '实际归还日期（NULL=未还）',
  status ENUM('borrowing','returned','overdue') DEFAULT 'borrowing' COMMENT '借阅状态',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  KEY idx_user (user_id),
  KEY idx_book (book_id),
  KEY idx_status (status),
  KEY idx_due_date (due_date),
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (book_id) REFERENCES books(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='借阅记录表';

CREATE TABLE reservations (
  id INT PRIMARY KEY AUTO_INCREMENT COMMENT '预约ID',
  user_id INT NOT NULL COMMENT '读者ID',
  book_id INT NOT NULL COMMENT '图书ID',
  reserve_date DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '预约日期',
  status ENUM('pending','fulfilled','cancelled') DEFAULT 'pending' COMMENT '预约状态',
  expire_date DATETIME COMMENT '过期时间',
  UNIQUE KEY uk_user_book (user_id, book_id),
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (book_id) REFERENCES books(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约记录表';

CREATE TABLE fines (
  id INT PRIMARY KEY AUTO_INCREMENT COMMENT '罚金ID',
  user_id INT NOT NULL COMMENT '读者ID',
  borrow_id INT NOT NULL COMMENT '关联借阅记录ID',
  amount DECIMAL(10,2) NOT NULL COMMENT '罚金金额',
  reason VARCHAR(200) COMMENT '罚金原因',
  status ENUM('unpaid','paid') DEFAULT 'unpaid' COMMENT '缴纳状态',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP comment '罚金生成时间',
  paid_at DATETIME DEFAULT NULL COMMENT '缴纳时间',
  KEY idx_user (user_id),
  KEY idx_status (status),
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (borrow_id) REFERENCES borrow_records(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='罚金记录表';

CREATE TABLE book_reviews (
  id INT PRIMARY KEY AUTO_INCREMENT COMMENT '书评ID',
  user_id INT NOT NULL COMMENT '读者ID',
  book_id INT NOT NULL COMMENT '图书ID',
  rating TINYINT NOT NULL COMMENT '评分1-5',
  content TEXT COMMENT '书评内容',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP comment '书评时间',
  KEY idx_book (book_id),
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (book_id) REFERENCES books(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='书评表';

INSERT INTO book_categories (name, description) VALUES
('文学小说', '中外文学、小说作品'),
('计算机科学', '编程、数据库、算法等'),
('历史哲学', '历史、哲学类书籍'),
('自然科学', '数学、物理、化学等'),
('经济管理', '经济学、管理学类');

INSERT INTO users (username, password, email, real_name, role) VALUES
('admin', '123456', 'admin@library.com', '系统管理员', 'admin');

INSERT INTO users (username, password, email, phone, real_name, role, max_borrow_count) VALUES
('zhangsan', '$2a$10$placeholder', 'zhangsan@test.com', '13800001111', '张三', 'reader', 5),
('lisi', '$2a$10$placeholder', 'lisi@test.com', '13800002222', '李四', 'reader', 5),
('wangwu', '$2a$10$placeholder', 'wangwu@test.com', '13800003333', '王五', 'reader', 3),
('zhaoliu', '$2a$10$placeholder', 'zhaoliu@test.com', '13800004444', '赵六', 'reader', 5),
('teacher01', '$2a$10$placeholder', 'teacher@school.com', '13800005555', '李老师', 'reader', 10);

INSERT INTO books (title, author, isbn, publisher, publish_year, category_id, total_copies, available_copies, location) VALUES
('深入理解计算机系统', 'Randal E.Bryant', '9787111544937', '机械工业出版社', 2016, 2, 3, 2, 'A-01-01'),
('算法导论', 'Thomas H.Cormen', '9787111407010', '机械工业出版社', 2013, 2, 2, 2, 'A-01-02'),
('MySQL必知必会', 'Ben Forta', '9787115240938', '人民邮电出版社', 2020, 2, 5, 4, 'A-02-01'),
('JavaScript高级程序设计', 'Matt Frisbie', '9787115545535', '人民邮电出版社', 2020, 2, 3, 3, 'A-02-02'),
('百年孤独', '加西亚·马尔克斯', '9787544253994', '南海出版公司', 2011, 1, 4, 3, 'B-01-01'),
('红楼梦', '曹雪芹', '9787020002207', '人民文学出版社', 1996, 1, 6, 5, 'B-01-02'),
('三体', '刘慈欣', '9787536692930', '重庆出版社', 2008, 1, 5, 4, 'B-02-01'),
('活着', '余华', '9787506365437', '作家出版社', 2012, 1, 3, 3, 'B-02-02'),
('人类简史', '尤瓦尔·赫拉利', '9787508647357', '中信出版社', 2014, 3, 3, 2, 'C-01-01'),
('明朝那些事儿', '当年明月', '9787210041134', '江西人民出版社', 2009, 3, 4, 4, 'C-01-02'),
('时间简史', '史蒂芬·霍金', '9787535732309', '湖南科技出版社', 2010, 4, 3, 3, 'D-01-01'),
('经济学原理', '曼昆', '9787301191365', '北京大学出版社', 2012, 5, 4, 4, 'E-01-01'),
('Spring实战', 'Craig Walls', '9787115417305', '人民邮电出版社', 2016, 2, 2, 1, 'A-03-01'),
('Java核心技术', 'Cay S.Horstmann', '9787111547426', '机械工业出版社', 2016, 2, 3, 2, 'A-03-02'),
('围城', '钱锺书', '9787020024759', '人民文学出版社', 1991, 1, 3, 3, 'B-03-01'),
('平凡的世界', '路遥', '9787530216781', '北京十月文艺出版社', 2012, 1, 5, 5, 'B-03-02'),
('中国哲学简史', '冯友兰', '9787301217495', '北京大学出版社', 2013, 3, 2, 2, 'C-02-01'),
('高等数学', '同济大学', '9787040396638', '高等教育出版社', 2014, 4, 8, 7, 'D-02-01'),
('Python编程从入门到实践', 'Eric Matthes', '9787115428028', '人民邮电出版社', 2016, 2, 4, 3, 'A-04-01'),
('设计模式', 'GoF', '9787111075752', '机械工业出版社', 2000, 2, 2, 2, 'A-04-02');

INSERT INTO borrow_records (user_id, book_id, borrow_date, due_date, status) VALUES
(2, 1, '2026-06-01 10:00:00', '2026-06-30 10:00:00', 'borrowing');

INSERT INTO borrow_records (user_id, book_id, borrow_date, due_date, status) VALUES
(3, 13, '2026-06-05 14:00:00', '2026-07-05 14:00:00', 'borrowing');

INSERT INTO borrow_records (user_id, book_id, borrow_date, due_date, return_date, status) VALUES
(4, 3, '2026-05-20 09:00:00', '2026-06-20 09:00:00', '2026-06-10 11:00:00', 'returned');

INSERT INTO users (username, password) VALUES ('admin', '123456');


