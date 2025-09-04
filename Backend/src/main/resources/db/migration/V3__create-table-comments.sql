-- Create comments table
CREATE TABLE comments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    incidentId UUID NOT NULL,
    author VARCHAR(255) NOT NULL,
    message VARCHAR(2000) NOT NULL,
    createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
); 