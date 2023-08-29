#!/bin/bash

# Get today's date in YYYYMMDD format
current_date=$(date +%Y%m%d)
echo "Today: $current_date"

# Calculate yesterday's date by subtracting 1 day from today's date
yesterday_date=$(date -d "$today_date - 1 day" +%Y%m%d)
echo "Yesterday: $yesterday_date"

backup_filename="backup_${yesterday_date}.sql"

# Check if the backup file with yesterday's date exists
if [ -f "$backup_filename" ]; then
  # Delete the older backup
  rm "$backup_filename"
  echo "Old backup deleted: $backup_filename"
fi

backup_filename="backup_${current_date}.sql"

# Run the docker cp command to perform a new backup
docker cp budget-buddy-db:/backup.sql "$backup_filename"
echo "New backup performed: $backup_filename"