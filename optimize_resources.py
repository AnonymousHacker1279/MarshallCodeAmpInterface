import os

png_file_locations = []
for root, dirs, files in os.walk('resources'):
	for file in files:
		if file.endswith('.png'):
			png_file_locations.append(os.path.join(root, file))

total_bytes = 0
for image in png_file_locations:
	old_file_size = os.path.getsize(f'{image}')
	total_bytes += old_file_size

	os.system(f'optipng.exe -q -o7 -zm1-9 -strip all {image}')

	new_file_size = os.path.getsize(f'{image}')
	if old_file_size > new_file_size:
		print(f'Reduced {image} from {old_file_size} bytes to {new_file_size} bytes.')

print(f'Total bytes saved: {total_bytes - sum([os.path.getsize(f"{image}") for image in png_file_locations])}')
