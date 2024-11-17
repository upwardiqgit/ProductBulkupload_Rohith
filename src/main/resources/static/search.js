document.addEventListener('DOMContentLoaded', async () => {
    // Enable/Disable search button based on form input
    const productNameInput = document.getElementById('productName');
    const productTypeSelect = document.getElementById('productType');
    const searchBtn = document.getElementById('searchBtn');

    function toggleSearchButton() {
        if (productNameInput.value.trim() && productTypeSelect.value.trim()) {
            searchBtn.disabled = false;
        } else {
            searchBtn.disabled = true;
        }
    }

    // Add event listeners to inputs
    productNameInput.addEventListener('input', toggleSearchButton);
    productTypeSelect.addEventListener('change', toggleSearchButton);

    // Populate product type dropdown
    try {
        const response = await fetch('/api/products/product-types');
        const productTypes = await response.json();
        const dropdown = document.getElementById('productType');

        if (!dropdown) {
            console.error("Dropdown element 'productType' not found.");
            return;
        }

        productTypes.forEach(type => {
            const option = document.createElement('option');
            option.value = type;
            option.textContent = type;
            dropdown.appendChild(option);
        });
    } catch (error) {
        console.error('Error fetching product types:', error.message);
    }

    // Search functionality
    document.getElementById('searchBtn').addEventListener('click', async () => {
        const productName = document.getElementById('productName').value;
        const productType = document.getElementById('productType').value;
        const tableBody = document.querySelector('#resultsTable tbody');
        const exportBtn = document.getElementById('exportBtn');

        try {
            // Fetch data from the updated search endpoint
            const response = await fetch(`/api/products/search?productName=${encodeURIComponent(productName)}&productType=${encodeURIComponent(productType)}`);

            if (!response.ok) {
                throw new Error('Search failed');
            }

            // Get the cumulative results from the response
            const products = await response.json();

            // Clear previous search results from the table
            tableBody.innerHTML = '';

            // Append the new results to the table
            products.forEach(product => {
                const row = `<tr>
                    <td>${product.productId}</td>
                    <td>${product.productName}</td>
                    <td>${product.productType}</td>
                    <td>${product.price}</td>
                </tr>`;
                tableBody.innerHTML += row;
            });

            // Show the export button if there are any results
            exportBtn.style.display = products.length > 0 ? 'block' : 'none';

        } catch (error) {
            alert('Error fetching data: ' + error.message);
        }
    });

    // Export to Excel functionality
    document.getElementById('exportBtn').addEventListener('click', async () => {
        try {
            // Trigger file download
            const exportUrl = `/api/products/export`;
            const link = document.createElement('a');
            link.href = exportUrl;
            link.download = 'searched_products.xlsx';
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);

            const exportMessage = document.getElementById('exportMessage');
            if (exportMessage) {
                exportMessage.style.color = 'green';
                exportMessage.textContent = 'Export started! Your file will download shortly.';
                setTimeout(() => {
                    exportMessage.textContent = '';
                }, 3000);
            }
        } catch (error) {
            alert('Error exporting data: ' + error.message);
        }
    });
});
