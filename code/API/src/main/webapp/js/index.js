const baseURL = window.location.protocol + "//" + window.location.hostname + ":8080/"

function generateRandomString() {
    var array = new Uint32Array(28);
    window.crypto.getRandomValues(array);
    return Array.from(array, dec => ('0' + dec.toString(16)).substr(-2)).join('');
}

function sha256(plain) {
    const encoder = new TextEncoder();
    const data = encoder.encode(plain);
    return window.crypto.subtle.digest('SHA-256', data);
}

function base64urlencode(str) {
    return btoa(String.fromCharCode.apply(null, new Uint8Array(str)))
        .replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '');
}

async function pkceChallengeFromVerifier(v) {
    hashed = await sha256(v);
    return base64urlencode(hashed);
}

function utf8_to_b64(str) {
    return window.btoa(encodeURIComponent(str).replace(/%([0-9A-F]{2})/g, function(match, p1) {
        return String.fromCharCode(parseInt(p1, 16));
    }));
}

window.onload = function() {
    document.getElementById("myButton").onclick = async function () {
        var mail = document.getElementById("inputEmail").value;
        var password = document.getElementById("inputPassword").value;

        if (!mail || !password) {
            alert("Veuillez remplir l'email et le mot de passe.");
            return;
        }

        var code_verifier = generateRandomString();
        var code_challenge = await pkceChallengeFromVerifier(code_verifier);

        var step = utf8_to_b64(mail + "#" + code_challenge);
        var step2 = "Bearer " + step;

        $.ajax({
            url: baseURL + 'api//authorize',
            type: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Pre-Authorization': step2
            },
            complete: function(data) {
                var signInId = data.responseJSON.signInId;
                console.log("SignInId recu:", signInId);

                let reqObj = { "mail": mail, "password": password, "signInId": signInId };

                $.ajax({
                    url: baseURL + 'api//authenticate',
                    type: 'POST',
                    data: JSON.stringify(reqObj),
                    dataType: 'json',
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/json'
                    },
                    success: function(data) {
                        console.log("AuthCode recu:", data.authCode);

                        var access = "Bearer " + utf8_to_b64(data.authCode + '#' + code_verifier);

                        $.ajax({
                            url: baseURL + 'api//oauth/token',
                            type: 'GET',
                            headers: {
                                'Accept': 'application/json',
                                'Content-Type': 'application/json',
                                'Post-Authorization': access
                            },
                            success: function(data) {
                                console.log("Token recu:", data);
                                localStorage.setItem("accesstoken", data.accessToken);
                                localStorage.setItem("refreshtoken", data.refreshToken);
                                localStorage.setItem("mail", mail);
                                location.href = "dashboardi.html";
                            },
                            error: function(err) {
                                console.error("Erreur token:", err.responseText);
                                alert("Erreur lors de la recuperation du token.");
                            }
                        });
                    },
                    error: function(err) {
                        console.error("Erreur authenticate:", err.responseText);
                        alert("Email ou mot de passe incorrect.");
                    }
                });
            }
        });
    };
}