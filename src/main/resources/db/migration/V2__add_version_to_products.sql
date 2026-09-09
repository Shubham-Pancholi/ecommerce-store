-- Add version column for Optimistic Locking
-- We set a default of 0 so that any existing products are initialized properly.
ALTER TABLE products 
ADD COLUMN version INTEGER NOT NULL DEFAULT 0;
