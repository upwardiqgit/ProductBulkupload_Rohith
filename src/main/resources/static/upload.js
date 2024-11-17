document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('loadBtn').addEventListener('click', async () => {
        const fileInput = document.getElementById('file');
        const formData = new FormData();
        formData.append('file', fileInput.files[0]);

        if (!fileInput.files[0] || !fileInput.files[0].name.endsWith('.xlsx')) {
            alert('Please select a valid .xlsx file');
            return;
        }

        try {
            const response = await fetch('/api/products/upload', { method: 'POST', body: formData });
            const products = await response.json();
            const tableBody = document.querySelector('#productTable tbody');

            if (!tableBody) {
                console.error("Table body element not found");
                return;
            }

            // Populate the table with the uploaded data
            tableBody.innerHTML = '';
            products.forEach(product => {
                const row = `<tr>
                    <td>${product.productId}</td>
                    <td>${product.productName}</td>
                    <td>${product.productType}</td>
                    <td>${product.price}</td>
                </tr>`;
                tableBody.innerHTML += row;
            });

            document.getElementById('saveBtn').style.display = 'block';
            const messageElement = document.getElementById('saveMessage');
            if (messageElement) {
                messageElement.innerHTML = ''; // Clear any previous messages
            }
        } catch (error) {
            alert('Error loading file: ' + error.message);
        }
    });

    document.getElementById('saveBtn').addEventListener('click', async () => {
        const tableRows = Array.from(document.querySelectorAll('#productTable tbody tr'));
        const products = tableRows.map(row => {
            const cells = row.querySelectorAll('td');
            return {
                productId: cells[0].innerText,
                productName: cells[1].innerText,
                productType: cells[2].innerText,
                price: parseFloat(cells[3].innerText),
            };
        });

        try {
            const response = await fetch('/api/products/save', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(products),
            });

            const messageElement = document.getElementById('saveMessage');
            if (!messageElement) {
                console.error("Message element not found");
                return;
            }

            if (response.ok) {
                messageElement.style.color = 'green';
                messageElement.innerHTML = 'Data saved successfully!';
            } else {
                messageElement.style.color = 'red';
                messageElement.innerHTML = 'Failed to save data. Please try again.';
            }
        } catch (error) {
            alert('Error saving data: ' + error.message);
        }
    });
});
