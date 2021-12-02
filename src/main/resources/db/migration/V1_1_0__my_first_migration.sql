CREATE TABLE IF NOT EXISTS comments (
                                        id INT8,
                                        comment TEXT NOT NULL,
                                        author TEXT NOT NULL,
                                        PRIMARY KEY (id)
);