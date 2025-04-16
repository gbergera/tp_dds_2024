document.addEventListener('DOMContentLoaded', function () {
    var maps = {};


    function initMap(containerId, selectId) {
        if (maps[containerId]) {
            maps[containerId].remove();
        }

        var map = L.map(containerId).setView([-34.6037, -58.3816], 12);

        L.tileLayer('https://api.mapbox.com/styles/v1/{id}/tiles/{z}/{x}/{y}?access_token={accessToken}&language=es', {
            maxZoom: 18,
            id: 'mapbox/streets-v11',
            tileSize: 512,
            zoomOffset: -1,
            accessToken: 'pk.eyJ1IjoianJhaWptYW4iLCJhIjoiY2x2MmVzanMxMGY5YTJybjVmbXh5MW8weSJ9.NZEa7b9BPMe8znzHHNH8Tg'
        }).addTo(map);


        var heladeraIcon = L.icon({
            iconUrl: '../img/heladeraActiva.png',
            iconSize: [38, 38],
            iconAnchor: [19, 38],
            popupAnchor: [0, -38]
        });



        fetch('/api/heladeras')
            .then(response => response.json())
            .then(heladeras => {
                console.log(heladeras); // Para depuración
                var selectHeladera = document.getElementById(selectId);
                var selectHeladeraOrigen = document.getElementById('selectHeladeraOrigen');
                var selectHeladeraDestino = document.getElementById('selectHeladeraDestino');
                var selectViandas = document.getElementById('selectViandasOrigen');


                // Asegúrate de que todos los select existan
                if (!selectHeladera) {
                    console.error('No se encontró el dropdown con el ID ' + selectId);
                    return;
                }
                if (!Array.isArray(heladeras)) {
                    heladeras = [heladeras];
                }
                if(selectHeladeraOrigen !== null || selectHeladeraDestino !== null || selectViandas !== null) {
                    selectHeladeraOrigen.innerHTML = '<option value="" disabled selected>Selecciona una heladera</option>';
                    selectHeladeraDestino.innerHTML = '<option value="" disabled selected>Selecciona una heladera</option>';
                    selectViandas.innerHTML = '<option value="" disabled>Selecciona una o más viandas</option>'; // Agregado
                }
                selectHeladera.innerHTML = '<option value="" disabled selected>Selecciona una heladera</option>'; // Limpiar selectHeladera


                heladeras.forEach(function (heladera) {
                    if (selectId !== 'selectHeladeraOrigen' && selectId !== 'selectHeladeraDestino'){
                        var optionGeneral = document.createElement('option');
                    optionGeneral.value = heladera.nombre;
                    optionGeneral.text = `${heladera.nombre} - ${heladera.direcciones?.[0]?.calle || 'Sin dirección'}`;
                    selectHeladera.appendChild(optionGeneral);
                }
                    if(selectHeladeraOrigen !== null || selectHeladeraDestino !== null || selectViandas !== null) {
                        // Crear el option para el selector de origen
                        var optionOrigen = document.createElement('option');
                        optionOrigen.value = heladera.nombre;
                        optionOrigen.text = `${heladera.nombre} - ${heladera.direcciones?.[0]?.calle || 'Sin dirección'}`;
                        selectHeladeraOrigen.appendChild(optionOrigen);

                        // Crear el option para el selector de destino
                        var optionDestino = document.createElement('option');
                        optionDestino.value = heladera.nombre;
                        optionDestino.text = `${heladera.nombre} - ${heladera.direcciones?.[0]?.calle || 'Sin dirección'}`;
                        selectHeladeraDestino.appendChild(optionDestino);
                    }
                });

                if(selectHeladeraOrigen !== null || selectHeladeraDestino !== null || selectViandas !== null) {
                    selectHeladeraOrigen.addEventListener('change', function () {
                        // Obtener el select de viandas
                        selectViandas.innerHTML = '<option value="" disabled>Selecciona una o más viandas</option>';

                        // Obtener la heladera de origen seleccionada
                        var selectedHeladera = heladeras.find(h => h.nombre === selectHeladeraOrigen.value);
                        if (selectedHeladera && selectedHeladera.viandas) {
                            selectedHeladera.viandas.forEach(function (vianda) {
                                var viandaOption = document.createElement('option');
                                viandaOption.value = vianda.id; // Asumiendo que vianda tiene un ID
                                viandaOption.text = `${vianda.comida.nombre}`;
                                selectViandas.appendChild(viandaOption);
                            });
                        }
                    });

                    selectHeladeraDestino.addEventListener('change', validarCapacidadViandas);
                    selectViandas.addEventListener('change', validarCapacidadViandas);

                    function validarCapacidadViandas() {
                        var selectedHeladeraDestino = heladeras.find(h => h.nombre === selectHeladeraDestino.value);

                        if (selectedHeladeraDestino) {
                            // Obtener la cantidad de viandas seleccionadas
                            var viandasSeleccionadas = Array.from(selectViandas.selectedOptions);
                            var cantidadSeleccionada = viandasSeleccionadas.length;

                            // Calcular la capacidad restante
                            var capacidadRestante = selectedHeladeraDestino.capacidad - selectedHeladeraDestino.viandas.length;

                            // Mostrar valores para depuración
                            console.log(`Capacidad de la heladera: ${capacidadRestante}, Cantidad Seleccionada: ${cantidadSeleccionada}`);

                            // Si se supera la capacidad, mostrar un mensaje y desmarcar la última opción seleccionada
                            if (cantidadSeleccionada > capacidadRestante) {
                                alert(`No puedes seleccionar más viandas que la capacidad de la heladera destino. Capacidad: ${capacidadRestante}`);

                                // Desmarcar la última opción seleccionada
                                var lastSelectedOption = viandasSeleccionadas[viandasSeleccionadas.length - 1];
                                if (lastSelectedOption) {
                                    lastSelectedOption.selected = false; // Desmarcar la última opción
                                }
                            }
                        }
                    }
                }


                heladeras.forEach(function (heladera) {
                    if (heladera.ubicaciones && heladera.ubicaciones.length > 0) {
                        var marker = L.marker([heladera.ubicaciones[0].latitud, heladera.ubicaciones[0].longitud], { icon: heladeraIcon }).addTo(map);


                        marker.on('click', function (e) {
                            e.originalEvent.preventDefault();
                            if (selectId === 'selectHeladeraOrigen') {
                                selectHeladeraOrigen.value = heladera.nombre;
                                selectHeladeraOrigen.dispatchEvent(new Event('change'));
                            } else if (selectId === 'selectHeladeraDestino') {
                                selectHeladeraDestino.value = heladera.nombre;
                                selectHeladeraDestino.dispatchEvent(new Event('change'));
                            }else{
                                selectHeladera.value = heladera.nombre;
                            }
                        });
                    }
                });

            })
            .catch(error => console.error('Error al obtener heladeras:', error));
    }


        function populateTiposFallas(selectId) {
        fetch('/api/tiposFallas')
            .then(response => response.json())
            .then(tiposFallas => {
                console.log(tiposFallas);  // Para depuración

                var selectTipoFalla = document.getElementById(selectId);
                if (!selectTipoFalla) {
                    console.error('No se encontró el dropdown con el ID ' + selectId);
                    return;
                }

                // Llenar el select de tipos de fallas con la descripción
                tiposFallas.forEach(function (tipoFalla) {
                    var option = document.createElement('option');
                    option.value = tipoFalla.id;  // Usar "id" como valor
                    option.text = tipoFalla.descripcion;  // Mostrar "descripcion" en lugar de "nombre"
                    selectTipoFalla.appendChild(option);
                });
            })
            .catch(error => console.error('Error al obtener tipos de fallas:', error));
    }

    function populateFallasOnly(selectId) {
        fetch('/api/tiposFallas/fallas')
            .then(response => response.json())
            .then(tiposFallas => {
                console.log(tiposFallas);  // Para depuración

                var selectTipoFalla = document.getElementById(selectId);
                if (!selectTipoFalla) {
                    console.error('No se encontró el dropdown con el ID ' + selectId);
                    return;
                }

                // Llenar el select de tipos de fallas con la descripción
                tiposFallas.forEach(function (tipoFalla) {
                    var option = document.createElement('option');
                    option.value = tipoFalla.id;  // Usar "id" como valor
                    option.text = tipoFalla.descripcion;  // Mostrar "descripcion" en lugar de "nombre"
                    selectTipoFalla.appendChild(option);
                });
            })
            .catch(error => console.error('Error al obtener tipos de fallas:', error));
    }


    $('.modal').on('shown.bs.modal', function () {
        var mapContainers = $(this).find('.map-modal');
        mapContainers.each(function () {
            var mapContainer = $(this);
            if (mapContainer.length) {
                var selectId = mapContainer.data('select-id');
                initMap(mapContainer.attr('id'), selectId);
            }
        });
    });

    initMap('mapRegistroFalla', 'selectHeladeraRegistroFalla');
    populateFallasOnly('tipo-falla');
});
