jQuery.ajax({
    method: "GET",
    url: "api/cart",
    success: function(resultData) {
        console.log(resultData)
        let cart = resultData.cart;
        let idDict = resultData.idCart;
        let priceDict = resultData.priceId;

        for(let title in cart) {
            let price = 14;
            let quantity = cart[title];
            let total = priceDict[idDict[title]] * quantity;
            let cartItemHTML = `
                    <tr id="${idDict[title]}">
                        <td>${title}</td>
                        <td class="product-price">$${priceDict[idDict[title]]}</td>
                        <td>
                            <button id="${idDict[title]}" class="decrease-quantity">-</button>
                            <input type="number" class="product-quantity" value="${quantity}" min="1">
                            <button id="${idDict[title]}" class="increase-quantity">+</button>
                        </td>
                        <td class="product-total">$${total}</td>
                        <td><button id="${idDict[title]}" class="remove-product">Remove</button></td>
                    </tr>
                `;

            $("#cartItems").append(cartItemHTML);
        }
    },
    error: (jqXHR, textStatus, errorThrown) => {
        console.error("AJAX error:", textStatus, errorThrown);
        console.log("Raw response:", jqXHR.responseText);
        alert("Error occurred: " + textStatus);
    },
});

$("#cartItems").on("click", ".increase-quantity", function() {
    let input = $(this).siblings(".product-quantity");
    input.val(parseInt(input.val()) + 1);
    updateTotalForProduct($(this).closest("tr"));

});
$("#cartItems").on("click", ".decrease-quantity", function() {
    updateTotalForProduct($(this).closest("tr"));

});


$(document).ready(function() {
    $(document).on('click', '.increase-quantity', function(event) {
        var data = { item: this.id, increase: "true", remove: "false"}
        let quantityInput = $(this).siblings('.product-quantity');
        let currentValue = parseInt(quantityInput.val(), 10); // Convert to integer
        quantityInput.val(currentValue);
        console.log(data)
        jQuery.ajax({
            method: "POST",
            data: data,
            url: "api/cart",
            success: (resultData) => {
                console.log(resultData);
            },
            error: (jqXHR, textStatus, errorThrown) => {
                console.error("AJAX error:", textStatus, errorThrown);
                alert("Error occurred: " + textStatus);
            },
        });
    });
});

$(document).ready(function() {
    $(document).on('click', '.decrease-quantity', function(event) {
        var data = { item: this.id, increase: "false", remove: "false"}
        let quantityInput = $(this).siblings('.product-quantity');

        let currentValue = parseInt(quantityInput.val(), 10);

        if (currentValue > 1) {
            quantityInput.val(currentValue - 1);
        }
        console.log(data)
        jQuery.ajax({
            method: "POST",
            data: data,
            url: "api/cart",
            success: (resultData) => {
                console.log(resultData);
            },
            error: (jqXHR, textStatus, errorThrown) => {
                console.error("AJAX error:", textStatus, errorThrown);
                alert("Error occurred: " + textStatus);
            },
        });
    });
});

function updateTotalForProduct(row) {
    let price = parseFloat(row.find(".product-price").text().replace("$", ""))
    let quantity = parseInt(row.find(".product-quantity").val());
    row.find(".product-total").text(`$${price * quantity}`);

}

$(document).ready(function() {
    $(document).on('click', '.remove-product', function(event) {
        var data = { item: this.id, increase: "false", remove: "true" }
        $(this).closest("tr").remove();

        console.log(data)
        jQuery.ajax({
            method: "POST",
            data: data,
            url: "api/cart",
            success: (resultData) => {
                console.log(resultData);
            },
            error: (jqXHR, textStatus, errorThrown) => {
                console.error("AJAX error:", textStatus, errorThrown);
                alert("Error occurred: " + textStatus);
            },
        });
    });
});
$(document).ready(function() {
    $(".btn-accent").click(function(event) {
        event.preventDefault();
        jQuery.ajax({
            method: "GET",
            url: "api/cart",
            success: function(resultData) {
                console.log(resultData)
                const errorMessageDiv = jQuery("#login_error_message");
                if(JSON.stringify(resultData["cart"]) === '{}') {
                    errorMessageDiv.text("Your cart is empty!");
                    errorMessageDiv.show();
                }
                else {
                    window.location.href = "payment.html";

                }
            },
            error: (jqXHR, textStatus, errorThrown) => {
                console.error("AJAX error:", textStatus, errorThrown);
                console.log("Raw response:", jqXHR.responseText);
                alert("Error occurred: " + textStatus);
            },
        });

    });
});