jQuery.ajax({
    method: "GET",
    url: "api/cart",
    success: function(resultData) {
        let cart = resultData.cart;



        // Loop through each cart item
        for(let id in cart) {
            let price = 14;
            let quantity = cart[id];
            let total = price * quantity;

            // Construct the HTML for the cart item
            let cartItemHTML = `
                    <tr id="${id}">
                        <td>${id}</td>
                        <td>$${price}</td>
                        <td>
                            <button class="decrease-quantity">-</button>
                            <input type="number" class="product-quantity" value="${quantity}" min="1">
                            <button class="increase-quantity">+</button>
                        </td>
                        <td class="product-total">$${total}</td>
                        <td><button class="remove-product">Remove</button></td>
                    </tr>
                `;

            // Append the cart item to the container
            $("#cartItems").append(cartItemHTML);
        }
    },
    error: (jqXHR, textStatus, errorThrown) => {
        console.error("AJAX error:", textStatus, errorThrown);
        alert("Error occurred: " + textStatus);
    },
});

$("#cartItems").on("click", ".increase-quantity", function() {
    let input = $(this).siblings(".product-quantity");
    input.val(parseInt(input.val()) + 1);

});

$("#cartItems").on("click", ".decrease-quantity", function() {
    let input = $(this).siblings(".product-quantity");
    if (parseInt(input.val()) > 1) {
        input.val(parseInt(input.val()) - 1);

    }
});

$("#cartItems").on("click", ".remove-product", function() {
    $(this).closest("tr").remove();

});