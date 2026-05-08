$directory = "c:\Users\sabso\IdeaProjects\pidevproject\testunitaire\AdminandAgentdashboard\GestionReservation\src\main\resources"

$count = 0
Get-ChildItem -Path $directory -Recurse -Include *.fxml, *.css | ForEach-Object {
    $bytes = [System.IO.File]::ReadAllBytes($_.FullName)
    if ($bytes.Length -ge 3 -and $bytes[0] -eq 0xEF -and $bytes[1] -eq 0xBB -and $bytes[2] -eq 0xBF) {
        Write-Host "Removing BOM from: $($_.FullName)"
        $newBytes = new-object byte[] ($bytes.Length - 3)
        [System.Array]::Copy($bytes, 3, $newBytes, 0, $newBytes.Length)
        [System.IO.File]::WriteAllBytes($_.FullName, $newBytes)
        $count++
    }
}

Write-Host "Done. Removed BOM from $count resources."
