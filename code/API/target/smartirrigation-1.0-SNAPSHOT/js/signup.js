const baseURL = window.location.protocol + "//" + window.location.hostname + ":8080/"

document.getElementById("signup").addEventListener("click", function() {
    var userName = $('#inputuserName').val();
    var mail = $('#inputEmail').val();
    var password = $('#inputPassword').val();

    if (!mail || !password || !userName) {
        alert('Tous les champs sont obligatoires.');
        return;
    }

    let reqObj = {
        "mail": mail,
        "userName": userName,
        "password": password,
        "permissionLevel": 1
    };

    $.ajax({
        url: baseURL + 'api/user',
        type: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        data: JSON.stringify(reqObj),
        success: function(data) {
            console.log('User created successfully.');
            alert('Compte créé avec succès ! Vous pouvez vous connecter.');
            location.href = "index.html";
        },
        error: function(xhr, status, error) {
            console.error('Error creating user:', xhr.responseText);
            alert('Erreur : ' + xhr.responseText);
        },
        complete: function() {
            console.log('Request completed.');
        }
    });
});