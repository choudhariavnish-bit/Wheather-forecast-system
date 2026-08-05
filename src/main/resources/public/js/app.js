/**
 * Location Portal Controller
 * Handles Location Cascades, Geolocation API, 3D Card Tilt, and Form Submission
 */

document.addEventListener('DOMContentLoaded', () => {
    let locationData = {};

    // DOM Elements
    const countrySelect = document.getElementById('select-country');
    const stateSelect = document.getElementById('select-state');
    const citySelect = document.getElementById('select-city');
    const detectBtn = document.getElementById('detect-location-btn');
    const pillLocationText = document.getElementById('pill-location-text');

    const loginForm = document.getElementById('login-form');
    const submitBtn = document.getElementById('submit-login-btn');
    const loginSpinner = document.getElementById('login-spinner');
    const toastAlert = document.getElementById('toast-message');
    const loginCard = document.getElementById('login-card');

    // 1. Fetch Locations Dataset from Java REST API
    fetch('/api/locations')
        .then(res => res.json())
        .then(data => {
            locationData = data;
            populateCountries();
        })
        .catch(err => console.error('Error loading location data:', err));

    function populateCountries() {
        countrySelect.innerHTML = '<option value="" disabled selected>Select Country</option>';
        Object.keys(locationData).sort().forEach(country => {
            const opt = document.createElement('option');
            opt.value = country;
            opt.textContent = country;
            countrySelect.appendChild(opt);
        });

        // Set Default selection (India -> Maharashtra -> Chh. Sambhajinagar)
        if (locationData['India']) {
            countrySelect.value = 'India';
            onCountryChange('Maharashtra', 'Chh. Sambhajinagar');
        }
    }

    function onCountryChange(defaultState = null, defaultCity = null) {
        const country = countrySelect.value;
        stateSelect.innerHTML = '<option value="" disabled selected>Select State</option>';
        citySelect.innerHTML = '<option value="" disabled selected>Select City</option>';
        citySelect.disabled = true;

        if (country && locationData[country]) {
            stateSelect.disabled = false;
            Object.keys(locationData[country]).sort().forEach(state => {
                const opt = document.createElement('option');
                opt.value = state;
                opt.textContent = state;
                stateSelect.appendChild(opt);
            });

            if (defaultState) {
                stateSelect.value = defaultState;
                onStateChange(defaultCity);
            }
        } else {
            stateSelect.disabled = true;
        }
        updatePill();
    }

    function onStateChange(defaultCity = null) {
        const country = countrySelect.value;
        const state = stateSelect.value;
        citySelect.innerHTML = '<option value="" disabled selected>Select City</option>';

        if (country && state && locationData[country][state]) {
            citySelect.disabled = false;
            locationData[country][state].sort().forEach(city => {
                const opt = document.createElement('option');
                opt.value = city;
                opt.textContent = city;
                citySelect.appendChild(opt);
            });

            if (defaultCity) {
                citySelect.value = defaultCity;
            }
        } else {
            citySelect.disabled = true;
        }
        updatePill();
    }

    function updatePill() {
        const city = citySelect.value || '';
        const state = stateSelect.value || '';
        const country = countrySelect.value || '';
        if (city && country) {
            pillLocationText.textContent = `${city}, ${(state ? state + ', ' : '')}${country}`;
        }
    }

    countrySelect.addEventListener('change', () => onCountryChange());
    stateSelect.addEventListener('change', () => onStateChange());
    citySelect.addEventListener('change', () => updatePill());

    // 2. GPS Auto-Detect Location
    detectBtn.addEventListener('click', () => {
        if (!navigator.geolocation) {
            showToast('Geolocation is not supported by your browser.', 'error');
            return;
        }

        detectBtn.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Detecting...';
        navigator.geolocation.getCurrentPosition(
            (pos) => {
                detectBtn.innerHTML = '<i class="fa-solid fa-check"></i> Detected';
                setTimeout(() => { detectBtn.innerHTML = '<i class="fa-solid fa-compass"></i> Auto GPS'; }, 3000);
                showToast('Location coordinates detected via GPS!', 'success');
            },
            () => {
                detectBtn.innerHTML = '<i class="fa-solid fa-compass"></i> Auto GPS';
                showToast('Unable to detect location. Please select manually.', 'error');
            }
        );
    });

    // 3. Interactive 3D Card Tilt Physics
    loginCard.addEventListener('mousemove', (e) => {
        const rect = loginCard.getBoundingClientRect();
        const x = e.clientX - rect.left - rect.width / 2;
        const y = e.clientY - rect.top - rect.height / 2;

        const rotateX = (-y / (rect.height / 2)) * 8;
        const rotateY = (x / (rect.width / 2)) * 8;

        loginCard.style.transform = `perspective(1000px) rotateX(${rotateX}deg) rotateY(${rotateY}deg) scale3d(1.02, 1.02, 1.02)`;
    });

    loginCard.addEventListener('mouseleave', () => {
        loginCard.style.transform = `perspective(1000px) rotateX(0deg) rotateY(0deg) scale3d(1, 1, 1)`;
    });

    // 4. Form Submission to Java Backend
    loginForm.addEventListener('submit', (e) => {
        e.preventDefault();

        const country = countrySelect.value;
        const state = stateSelect.value;
        const city = citySelect.value;

        submitBtn.disabled = true;
        loginSpinner.classList.remove('hidden');
        document.querySelector('.btn-text').textContent = 'Confirming...';

        fetch('/api/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ country, state, city })
        })
        .then(res => res.json())
        .then(res => {
            submitBtn.disabled = false;
            loginSpinner.classList.add('hidden');
            document.querySelector('.btn-text').textContent = 'Confirm Location';

            if (res.success) {
                showToast(`Location confirmed for ${city}, ${country}!`, 'success');
            } else {
                showToast(res.message, 'error');
            }
        })
        .catch(err => {
            submitBtn.disabled = false;
            loginSpinner.classList.add('hidden');
            document.querySelector('.btn-text').textContent = 'Confirm Location';
            showToast('Server error during connection.', 'error');
        });
    });

    function showToast(msg, type) {
        toastAlert.textContent = msg;
        toastAlert.className = `toast-alert ${type}`;
        toastAlert.classList.remove('hidden');
        setTimeout(() => { toastAlert.classList.add('hidden'); }, 4000);
    }
});
