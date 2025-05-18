# Set the author name for all commits
$authorName = "John Feng"

# Function to get current timestamp in specified format
function Get-Timestamp {
    return (Get-Date -Format 'yyyyMMdd-HHmmss')
}

# Update main repository references to submodules
git add .

# Create timestamped commit message for main repository
$timestamp = Get-Timestamp
$mainCommitMessage = "$authorName $timestamp"

# Commit changes in main repository
git commit -m $mainCommitMessage

# Push changes to main branch of main repository
git push origin main

Write-Host "All changes have been pushed successfully."