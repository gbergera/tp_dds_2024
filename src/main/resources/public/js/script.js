function showInputs() {
    var tipoPersona = document.getElementById("tipoPersona").value;
    var formFisica = document.getElementById("form-persona-fisica");
    var formJuridica = document.getElementById("form-persona-juridica");

    if (tipoPersona === 'fisica') {
            formFisica.style.display = 'block';
            formJuridica.style.display = 'none';
            setRequiredAttributes(formFisica, true);
            setRequiredAttributes(formJuridica, false);

        } else if (tipoPersona === 'juridica') {
            formFisica.style.display = 'none';
            formJuridica.style.display = 'block';
            setRequiredAttributes(formFisica, false);
            setRequiredAttributes(formJuridica, true);
        } else {
            formFisica.style.display = 'none';
            formJuridica.style.display = 'none';
            setRequiredAttributes(formFisica, false);
            setRequiredAttributes(formJuridica, false);
        }

        toggleTelegramFields();
        toggleTelegramFieldsJuridica();

}

function toggleTelegramFields() {
    const metodoNotificacion = document.getElementById('metodo-notificacion-fisica').value;
    const telegramFields = document.getElementsByClassName('telegram-fields-fisica');
    for (let i = 0; i < telegramFields.length; i++) {
        telegramFields[i].style.display = metodoNotificacion === 'TELEGRAM' ? 'block' : 'none';
        if (metodoNotificacion === 'TELEGRAM') {
            telegramFields[i].querySelector('input').setAttribute('required', 'required');
        } else {
            telegramFields[i].querySelector('input').removeAttribute('required');
        }
    }
}

function toggleTelegramFieldsJuridica() {
    const metodoNotificacion = document.getElementById('metodo-notificacion-juridica').value;
    const telegramFields = document.getElementsByClassName('telegram-fields-juridica');
    for (let i = 0; i < telegramFields.length; i++) {
        telegramFields[i].style.display = metodoNotificacion === 'TELEGRAM' ? 'block' : 'none';
        if (metodoNotificacion === 'TELEGRAM') {
            telegramFields[i].querySelector('input').setAttribute('required', 'required');
        } else {
            telegramFields[i].querySelector('input').removeAttribute('required');
        }
    }
}

function setRequiredAttributes(form, isRequired) {
    let inputs = form.querySelectorAll('input, select');
    inputs.forEach(input => {
        if (isRequired) {
            input.setAttribute('required', 'required');
        } else {
            input.removeAttribute('required');
        }
    });
}

function showDocInputs() {
    const selectedValue = document.getElementById('tipoDocumento').value;
    const inputContainer = document.getElementById('DocumentoInputContainer');

    inputContainer.innerHTML = '';

    if (selectedValue !== 'NADA') {
        const newInput = document.createElement('input');
        newInput.type = 'number';
        newInput.className = 'form-control';
        newInput.placeholder = `Ingrese su numero de ${selectedValue}`;
        newInput.name = 'nroDoc';

        inputContainer.appendChild(newInput);
    }
}

function toggleSituacionInput() {
    const situacionValue = document.querySelector('input[name="situacion"]:checked').value;
    const inputContainer = document.getElementById('SituacioninputContainer');
    inputContainer.innerHTML = '';

    if (situacionValue === 'CON_DOMICILIO') {
        // Crear campos de Provincia, Municipio, Localidad, Calle y Número
        const provinciaDiv = document.createElement('div');
        provinciaDiv.className = 'col-md-12 mb-3';
        provinciaDiv.innerHTML = `
                <label for="provincia" class="fw-semibold">Provincia</label>
                <select name="provincia" class="form-select" id="direccion-provincia-juridica" required onchange="cargarMunicipios('juridica')">
                    <option selected disabled value="">Seleccionar...</option>
                </select>`;
        inputContainer.appendChild(provinciaDiv);

        const municipioDiv = document.createElement('div');
        municipioDiv.className = 'col-md-12 mb-3';
        municipioDiv.innerHTML = `
                <label for="municipio" class="fw-semibold">Municipio</label>
                <select name="municipio" class="form-select" id="direccion-municipio-juridica" required onchange="cargarLocalidades('juridica')">
                    <option selected disabled value="">Seleccionar...</option>
                </select>`;
        inputContainer.appendChild(municipioDiv);

        const localidadDiv = document.createElement('div');
        localidadDiv.className = 'col-md-12 mb-3';
        localidadDiv.innerHTML = `
                <label for="localidad" class="fw-semibold">Localidad</label>
                <select name="localidad" class="form-select" id="direccion-localidad-juridica" required>
                    <option selected disabled value="">Seleccionar...</option>
                </select>`;
        inputContainer.appendChild(localidadDiv);

        const calleDiv = document.createElement('div');
        calleDiv.className = 'col-md-12 mb-3';
        calleDiv.innerHTML = `
                <label for="calle" class="fw-semibold">Calle</label>
                <input name="calle" type="text" class="form-control" id="calle" placeholder="Ingrese la calle" required>`;
        inputContainer.appendChild(calleDiv);

        const numeroDiv = document.createElement('div');
        numeroDiv.className = 'col-md-12 mb-3';
        numeroDiv.innerHTML = `
                <label for="numero" class="fw-semibold">Número</label>
                <input name="numero" type="number" class="form-control" id="numero" placeholder="Ingrese el número" required>`;
        inputContainer.appendChild(numeroDiv);

        // Cargar provincias en los selects generados dinámicamente
        cargarProvincias();
    }
}


document.addEventListener('DOMContentLoaded', function() {
    const radioButtons = document.querySelectorAll('input[name="menores"]');
    const addMoreButton = document.getElementById('addMoreButton');

    radioButtons.forEach(radio => {
        radio.addEventListener('change', function() {
            showInputsMenores();
            toggleAddMoreButton();
        });
    });

    addMoreButton.addEventListener('click', addMoreInputs);
});



function showInputsMenores() {
    const selectedValue = document.querySelector('input[name="menores"]:checked').value;
    const inputContainer = document.getElementById('inputContainerMenores');

    inputContainer.innerHTML = ''; // Limpiar los inputs anteriores

    if (selectedValue === 'si') {
        addMoreInputs(); // Añadir el primer conjunto de inputs
    }
}

function toggleAddMoreButton() {
    const selectedValue = document.querySelector('input[name="menores"]:checked').value;
    const addMoreButton = document.getElementById('addMoreButton');

    if (selectedValue === 'si') {
        addMoreButton.style.display = 'block'; // Mostrar el botón de añadir más menores
    } else {
        addMoreButton.style.display = 'none'; // Ocultar si no es necesario
    }
}

function addMoreInputs() {
    const inputContainer = document.getElementById('inputContainerMenores');
    const newInputSet = document.createElement('div');
    newInputSet.className = 'menor-input-set';

    // Crear el input para el nombre del menor
    const textInput = document.createElement('input');
    textInput.type = 'text';
    textInput.className = 'form-control';
    textInput.placeholder = 'Ingrese el nombre del Menor';
    textInput.name = 'menoresNombres[]'; // Añadir el atributo name como array

    // Crear el input para la fecha de nacimiento del menor
    const dateInput = document.createElement('input');
    dateInput.type = 'date';
    dateInput.className = 'form-control';
    dateInput.placeholder = 'Ingrese la fecha de nacimiento del menor';
    dateInput.name = 'menoresEdades[]'; // Añadir el atributo name como array

    // Añadir los inputs al contenedor
    newInputSet.appendChild(textInput);
    newInputSet.appendChild(dateInput);

    inputContainer.appendChild(newInputSet); // Añadir el nuevo conjunto al DOM
}

function toggleRequiredFields(form, fieldIds, isRequired) {
    fieldIds.forEach(function(id) {
        var input = form.querySelector('#' + id);
        if (input) {
            if (isRequired) {
                input.setAttribute('required', 'required');
            } else {
                input.removeAttribute('required');
            }
        }
    });
}

function importCSV() {
    const input = document.getElementById('csvFile');
    const file = input.files[0];

    if (file) {
        const reader = new FileReader();
        reader.onload = function(event) {
            const text = event.target.result;
            console.log(text); // For now, just log the CSV content
            alert('CSV file imported successfully!');
        };
        reader.readAsText(file);
    } else {
        alert('Please select a CSV file first.');
    }
}
document.getElementById('imageInput').addEventListener('change', function(event) {
    const imagePreview = document.getElementById('imagePreview');
    imagePreview.innerHTML = ''; // Clear any existing images

    const files = event.target.files; // Get the selected files

    if (files.length > 0) {
        Array.from(files).forEach(file => {
            if (file.type.startsWith('image/')) { // Ensure the file is an image
                const reader = new FileReader();
                reader.onload = function(e) {
                    const img = document.createElement('img');
                    img.src = e.target.result;
                    img.style.width = '100px'; // Set the width of the image
                    img.style.marginRight = '10px'; // Add some space between images
                    img.style.marginBottom = '10px'; // Add space below images
                    img.style.border = '1px solid #ddd'; // Optional: border for better visibility
                    img.style.borderRadius = '4px'; // Optional: rounded corners
                    imagePreview.appendChild(img);
                };
                reader.readAsDataURL(file); // Read the file as a Data URL
            }
        });
    }
});

function closeAndRedirect(modalId) {
    const modal = bootstrap.Modal.getInstance(document.getElementById(modalId));
    modal.hide();

   // window.location.href = '/dashboard';
}

document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('enviarDonacionDinero').addEventListener('click', function() {
        closeAndRedirect('modalDonacionDinero');
    });

    document.getElementById('enviarDonacionVianda').addEventListener('click', function() {
        closeAndRedirect('modalDonacionVianda');
    });

    document.getElementById('realizarDistribucion').addEventListener('click', function() {
        closeAndRedirect('modalDistribucionVianda');
    });

    document.getElementById('clickEncargarse').addEventListener('click', function() {
        if (validateForm()) {
                closeAndRedirect('modalEncargarseDeHeladera');
        }
    });

    document.getElementById('clickOfrecer').addEventListener('click', function() {
        closeAndRedirect('modalOfrecerCosas');
    });

    document.getElementById('clickEntregarTarjetas').addEventListener('click', function() {
        closeAndRedirect('modalEntregarTarjetas');
    });
});
$(document).ready(function() {
    // Cargar heladeras y tipos de fallas al cargar la página
    fetchHeladeras();
    fetchTiposFallas(); // Nueva función para obtener tipos de fallas

    function fetchHeladeras() {
        fetch('/api/heladeras')
            .then(response => response.json())  // Parse JSON
            .then(data => {
                const selectHeladera = $('#selectHeladeraRegistroFalla');

                if (!Array.isArray(data)) {
                    data = [data];
                }

                // Ahora recorremos los datos para agregarlos al select
                data.forEach(heladera => {
                    const option = `<option value="${heladera.id}">${heladera.nombre}</option>`;
                    selectHeladera.append(option);
                });
            })
            .catch(error => console.error('Error fetching heladeras:', error));
    }

    // Nueva función para consultar los tipos de fallas
    function fetchTiposFallas() {
        fetch('/api/tiposFallas')
            .then(response => response.json())  // Parse JSON
            .then(data => {
                const selectTipoFalla = $('#tipo-falla');  // Seleccionamos el elemento del DOM

                if (!Array.isArray(data)) {
                    data = [data];
                }

                // Recorrer los tipos de fallas y agregar opciones al select
                data.forEach(falla => {
                    const option = `<option value="${falla.id}">${falla.nombre}</option>`;
                    selectTipoFalla.append(option);
                });
            })
            .catch(error => console.error('Error fetching tipos de fallas:', error));
    }
});

function validateForm() {
    const form = document.getElementById('heladeraForm');
        const inputs = form.querySelectorAll('input[required], select[required]');
        let isValid = true;

        inputs.forEach(input => {
            const errorDiv = document.getElementById(input.id + 'Error');
            if (!input.value.trim()) {
                errorDiv.textContent = `El campo ${input.name} es obligatorio.`;
                isValid = false;
            } else {
                errorDiv.textContent = '';
            }
        });

        return isValid;
}



