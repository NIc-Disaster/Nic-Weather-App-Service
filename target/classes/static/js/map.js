const hoverCard = document.getElementById("hover-card");
const stationCard = document.getElementById("station-card");
const historyForm = document.getElementById("history-form");
const historyTableBody = document.getElementById("history-table-body");
const districtSelect = document.getElementById("districtSelect");
const stationSelect = document.getElementById("stationSelect");
const daysSelect = document.getElementById("days");
const menuButtons = document.querySelectorAll(".menu-button");
const districtMapContainer = document.getElementById("district-map-container");

const DISTRICT_DEFAULT_STYLE = "fill:#cfe2ff;stroke:#0d6efd;stroke-width:2;cursor:pointer;transition:all .2s ease-in-out;";
const DISTRICT_ACTIVE_STYLE = "fill:#0d6efd;stroke:#083b8a;stroke-width:2;cursor:pointer;transition:all .2s ease-in-out;";
const SVG_DISTRICT_CODE_MAP = {
    WEST_JAINTIA_HILLS: "D-EAST",
    EAST_JAINTIA_HILLS: "D-EAST",
    RI_BHOI: "D-CENTRAL",
    EAST_KHASI_HILLS: "D-CENTRAL",
    WEST_KHASI_HILLS: "D-WEST",
    SOUTH_WEST_KHASI_HILLS: "D-SOUTH",
    NORTH_GARO_HILLS: "D-NORTH",
    EAST_GARO_HILLS: "D-NORTH",
    WEST_GARO_HILLS: "D-WEST",
    SOUTH_WEST_GARO_HILLS: "D-WEST",
    SOUTH_GARO_HILLS: "D-SOUTH"
};

let mapDataByApiCode = {};
let districtNodes = [];
let districtUiIds = [];

const slugify = (value) => String(value || "")
    .trim()
    .replace(/_+/g, " ")
    .replace(/[^a-zA-Z0-9]+/g, "_")
    .replace(/^_+|_+$/g, "")
    .toUpperCase();

const toDisplayName = (districtUiId) => String(districtUiId || "")
    .replace(/_/g, " ")
    .replace(/\s+/g, " ")
    .trim();

const getApiCodeForUiDistrict = (districtUiId) => {
    const key = slugify(districtUiId);
    return SVG_DISTRICT_CODE_MAP[key] || "D-CENTRAL";
};

const ensureTooltipNode = (node) => {
    let titleNode = node.querySelector("title");
    if (!titleNode) {
        titleNode = document.createElementNS("http://www.w3.org/2000/svg", "title");
        node.appendChild(titleNode);
    }
    return titleNode;
};

const setDistrictNodeState = (node, active) => {
    node.setAttribute("style", active ? DISTRICT_ACTIVE_STYLE : DISTRICT_DEFAULT_STYLE);
};

const activateMenuButton = (targetId) => {
    menuButtons.forEach((button) => {
        button.classList.toggle("active", button.dataset.target === targetId);
    });
};

const wireTopMenu = () => {
    menuButtons.forEach((button) => {
        button.addEventListener("click", () => {
            const targetId = button.dataset.target;
            const targetElement = document.getElementById(targetId);
            if (targetElement) {
                targetElement.scrollIntoView({behavior: "smooth", block: "start"});
            }
            activateMenuButton(targetId);
        });
    });
};

const buildHoverHtml = (districtUiId, districtData) => `
    <h3 class="h6 mb-2">${toDisplayName(districtUiId)}</h3>
    <div><strong>Date:</strong> ${districtData.observationDate}</div>
    <div><strong>Temperature:</strong> ${districtData.temperatureCelsius} C</div>
    <div><strong>Humidity:</strong> ${districtData.humidityPercent}%</div>
    <div><strong>Condition:</strong> ${districtData.weatherCondition}</div>
    <div><strong>Nowcast:</strong> ${districtData.nowcastSummary}</div>
`;

const buildStationHtml = (districtUiId, districtData) => `
    <div><strong>Station:</strong> ${toDisplayName(districtUiId)} Station</div>
    <div><strong>Date:</strong> ${districtData.observationDate}</div>
    <div><strong>Temperature:</strong> ${districtData.temperatureCelsius} C</div>
    <div><strong>Humidity:</strong> ${districtData.humidityPercent}%</div>
    <div><strong>Condition:</strong> ${districtData.weatherCondition}</div>
`;

const activateDistrict = (districtUiId) => {
    districtNodes.forEach((node) => {
        const active = node.dataset.uiDistrictId === districtUiId;
        setDistrictNodeState(node, active);
    });
};

const applyDistrictTooltips = () => {
    districtNodes.forEach((node) => {
        const districtUiId = node.dataset.uiDistrictId;
        const apiCode = node.dataset.apiDistrictCode;
        const districtData = mapDataByApiCode[apiCode];
        const tooltipText = districtData
            ? `${toDisplayName(districtUiId)}: ${districtData.temperatureCelsius} C`
            : `${toDisplayName(districtUiId)}: No weather data`;
        ensureTooltipNode(node).textContent = tooltipText;
    });
};

const loadMapData = async () => {
    const response = await fetch("/api/weather/map");
    const payload = await response.json();
    mapDataByApiCode = payload.reduce((acc, point) => {
        acc[point.districtCode] = point;
        return acc;
    }, {});
    applyDistrictTooltips();
};

const loadHistoryByUiDistrict = async (districtUiId, days) => {
    const apiCode = getApiCodeForUiDistrict(districtUiId);
    const response = await fetch(`/api/weather/district/${apiCode}/history?days=${days}`);
    const payload = await response.json();

    if (!payload.history || payload.history.length === 0) {
        historyTableBody.innerHTML = `
            <tr>
                <td colspan="3" class="text-secondary">No history found for this district.</td>
            </tr>
        `;
        return;
    }

    historyTableBody.innerHTML = payload.history.map((point) => `
        <tr>
            <td>${point.date}</td>
            <td>${point.temperatureCelsius} C</td>
            <td>
                <div>${point.weatherCondition}</div>
                <small class="text-secondary">${point.nowcastSummary}</small>
            </td>
        </tr>
    `).join("");
};

const updateCardsForDistrict = (districtUiId) => {
    const apiCode = getApiCodeForUiDistrict(districtUiId);
    const districtData = mapDataByApiCode[apiCode];
    if (!districtData) {
        hoverCard.innerHTML = `<span class="text-danger">No DB data for ${toDisplayName(districtUiId)}</span>`;
        stationCard.innerHTML = `<span class="text-danger">No station data for ${toDisplayName(districtUiId)}</span>`;
        return;
    }

    hoverCard.innerHTML = buildHoverHtml(districtUiId, districtData);
    stationCard.innerHTML = buildStationHtml(districtUiId, districtData);
};

const populateSelectorsFromMap = () => {
    const optionsHtml = districtUiIds.map((districtUiId) => `
        <option value="${districtUiId}">${toDisplayName(districtUiId)}</option>
    `).join("");

    districtSelect.innerHTML = `<option value="">Select district</option>${optionsHtml}`;
    stationSelect.innerHTML = `<option value="">Select station</option>${districtUiIds.map((districtUiId) => `
        <option value="${districtUiId}">${toDisplayName(districtUiId)} Station</option>
    `).join("")}`;
};

const wireMapHover = () => {
    districtNodes.forEach((districtNode) => {
        districtNode.addEventListener("mouseenter", () => {
            const districtUiId = districtNode.dataset.uiDistrictId;
            activateDistrict(districtUiId);
            districtSelect.value = districtUiId;
            stationSelect.value = districtUiId;
            updateCardsForDistrict(districtUiId);
            loadHistoryByUiDistrict(districtUiId, daysSelect.value);
        });
    });
};

const wireSelectors = () => {
    historyForm.addEventListener("submit", async (event) => {
        event.preventDefault();
        const districtUiId = districtSelect.value;
        if (!districtUiId) {
            historyTableBody.innerHTML = `
                <tr><td colspan="3" class="text-danger">Please select a district first.</td></tr>
            `;
            return;
        }
        activateDistrict(districtUiId);
        updateCardsForDistrict(districtUiId);
        await loadHistoryByUiDistrict(districtUiId, daysSelect.value);
    });

    districtSelect.addEventListener("change", async () => {
        const districtUiId = districtSelect.value;
        if (!districtUiId) {
            historyTableBody.innerHTML = `
                <tr><td colspan="3" class="text-secondary">Select a district to load history.</td></tr>
            `;
            return;
        }
        stationSelect.value = districtUiId;
        activateDistrict(districtUiId);
        updateCardsForDistrict(districtUiId);
        await loadHistoryByUiDistrict(districtUiId, daysSelect.value);
    });

    stationSelect.addEventListener("change", async () => {
        const districtUiId = stationSelect.value;
        if (!districtUiId) {
            stationCard.innerHTML = `<span class="text-secondary">Select a station to see current weather details.</span>`;
            return;
        }
        districtSelect.value = districtUiId;
        activateDistrict(districtUiId);
        updateCardsForDistrict(districtUiId);
        await loadHistoryByUiDistrict(districtUiId, daysSelect.value);
    });

    daysSelect.addEventListener("change", async () => {
        const districtUiId = districtSelect.value;
        if (!districtUiId) {
            return;
        }
        await loadHistoryByUiDistrict(districtUiId, daysSelect.value);
    });
};

const injectInlineSvg = async () => {
    const response = await fetch("/meghalaya_districts.svg");
    const svgMarkup = await response.text();
    const parser = new DOMParser();
    const svgDoc = parser.parseFromString(svgMarkup, "image/svg+xml");
    const svgElement = svgDoc.querySelector("svg");
    if (!svgElement) {
        throw new Error("SVG not found");
    }
    svgElement.setAttribute("id", "district-map");
    svgElement.setAttribute("role", "img");
    svgElement.setAttribute("aria-label", "District weather map");
    districtMapContainer.innerHTML = "";
    districtMapContainer.appendChild(svgElement);
};

const setupDistrictNodes = () => {
    districtNodes = Array.from(document.querySelectorAll("#district-map path[id]"));
    districtNodes = districtNodes.filter((node) => node.id && node.id !== "mesh_polyfill");

    districtNodes.forEach((node) => {
        const districtUiId = slugify(node.id);
        const apiCode = getApiCodeForUiDistrict(districtUiId);
        node.dataset.uiDistrictId = districtUiId;
        node.dataset.apiDistrictCode = apiCode;
        setDistrictNodeState(node, false);
    });

    districtUiIds = [...new Set(districtNodes.map((node) => node.dataset.uiDistrictId))];
};

const init = async () => {
    try {
        wireTopMenu();
        await injectInlineSvg();
        setupDistrictNodes();
        populateSelectorsFromMap();
        wireMapHover();
        wireSelectors();
        await loadMapData();

        const firstDistrictUiId = districtUiIds[0];
        if (!firstDistrictUiId) {
            return;
        }

        districtSelect.value = firstDistrictUiId;
        stationSelect.value = firstDistrictUiId;
        activateDistrict(firstDistrictUiId);
        updateCardsForDistrict(firstDistrictUiId);
        await loadHistoryByUiDistrict(firstDistrictUiId, daysSelect.value);
    } catch (error) {
        hoverCard.innerHTML = `<span class="text-danger">Could not load weather map data.</span>`;
        stationCard.innerHTML = `<span class="text-danger">Could not load station data.</span>`;
    }
};

init();
