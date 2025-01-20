#!/bin/bash

# Prepare the VM to perform the cronjob
# chmod +x backup.sh
# crontab -e
# 15 10 * * * /bin/bash /home/ubuntu/dev/budget-buddy-api/backup.sh
# crontab -l

# Get today's date in YYYYMMDD format
current_date=$(date +%Y%m%d)
echo "Today: $current_date"

# Calculate yesterday's date by subtracting 1 day from today's date
yesterday_date=$(date -d "$today_date - 1 day" +%Y%m%d)
echo "Yesterday: $yesterday_date"

old_backup_filename="backup_${yesterday_date}.sql"
echo "Yesterday backup file: $old_backup_filename"

# Check if the backup file with yesterday's date exists
if [ -f "$old_backup_filename" ]; then
  # Delete the older backup
  rm -rf "$old_backup_filename"
  echo "Old backup deleted: $old_backup_filename"
fi

backup_filename="backup_${current_date}.sql"
echo "Current backup file: $backup_filename"

echo "Starting database dump..."
# Run the docker exec command to perform a new dump inside the MYSQL container
sudo docker exec -it budget-buddy-db /bin/bash -c "mysqldump -h localhost -u root -ppassword budget-buddy > backup.sql"
echo "Database dump performed inside docker container"

# Run the docker cp command to perform a new backup
sudo docker cp budget-buddy-db:/backup.sql "$backup_filename"
echo "New backup performed: $backup_filename"

echo "Moving the backup into the Google Drive folder..."
mv $backup_filename /home/ubuntu/backup/Linux/backup

# To import the backup to MYSQL
# mysql -u user -ppassword budget-buddy < /backup.sql
