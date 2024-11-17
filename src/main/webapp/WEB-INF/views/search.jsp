<!DOCTYPE html>
<html>
<head>
    <title>Search Products</title>
        <link rel="stylesheet" href="/styles.css">
    <style>
        .logo {
            display: block;
            margin: 0 auto;
            max-width: 250px; /* Adjust the size of the logo */
        }
        .navbar {
            margin-top: 10px; /* Space between the logo and navbar */
        }
        h1 {
            text-align: center; /* Center the heading */
            margin-top: 20px; /* Add some space above the heading */
        }
    </style>
</head>
<body>
 
    <img src="/logo.jpg" alt="UpwardIQ Logo" class="logo">

    <!-- Navigation Bar -->
    <div class="navbar">
        <a href="/">Home</a>
        <a href="/upload">Upload Product</a>
        <a href="/search">Search Product</a>
    </div>

    <h2>Search Products</h2>

    <form id="searchForm">
        <label for="productName">Product Name:</label>
        <input type="text" id="productName" name="productName" required>

        <label for="productType">Product Type:</label>
        <select id="productType" name="productType">
            <option value="">-- Select Product Type --</option>
        </select>

        <button type="button" id="searchBtn">Search</button>
    </form>

    <h3>Search Results</h3>
    <table id="resultsTable">
        <thead>
            <tr>
                <th>Product ID</th>
                <th>Product Name</th>
                <th>Product Type</th>
                <th>Price</th>
            </tr>
        </thead>
        <tbody>
            <!-- Dynamically populated -->
        </tbody>
    </table>

<div class="export-section">
    <button type="button" id="exportBtn" style="display: none;">Export to Excel</button>
    <p id="exportMessage" class="message"></p>
</div>
    <script src="/search.js"></script>
</body>
</html>
