import os

def remove_bom(file_path):
    try:
        with open(file_path, 'rb') as f:
            content = f.read()
        
        # Check if file has UTF-8 BOM
        if content.startswith(b'\xef\xbb\xbf'):
            print(f"Removing BOM from: {file_path}")
            # Write back without BOM
            with open(file_path, 'wb') as f:
                f.write(content[3:])
            return True
        return False
    except Exception as e:
        print(f"Error processing {file_path}: {e}")
        return False

# Directory to scan
directory = r"c:\Users\sabso\IdeaProjects\pidevproject\testunitaire\AdminandAgentdashboard\GestionReservation\src\main\java"

fixed_count = 0
for root, dirs, files in os.walk(directory):
    for file in files:
        if file.endswith('.java'):
            file_path = os.path.join(root, file)
            if remove_bom(file_path):
                fixed_count += 1

print(f"Done. Removed BOM from {fixed_count} files.")
