-- Create incidents table
CREATE TABLE incidents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(120) NOT NULL,
    description VARCHAR(5000),
    priority VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    responsibleEmail VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create incident_tags table for ElementCollection
CREATE TABLE incident_tags (
    incident_id UUID NOT NULL,
    tag VARCHAR(255) NOT NULL,
    FOREIGN KEY (incident_id) REFERENCES incidents(id) ON DELETE CASCADE
); 