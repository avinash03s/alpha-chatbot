const API_URL = "/api/ai/ask";

const questionInput = document.getElementById("question");
const sendBtn       = document.getElementById("sendBtn");
const chatBody      = document.getElementById("chatBody");
const themeBtn      = document.getElementById("themeBtn");
const themeIcon     = document.getElementById("themeIcon");
const refreshBtn    = document.getElementById("refreshBtn");
const brandBtn      = document.getElementById("brandBtn");
const brandPopup    = document.getElementById("brandPopup");

const savedTheme = localStorage.getItem("theme");
if (savedTheme === "light") {
    document.body.classList.add("light-theme");
    themeIcon.className = "ti ti-moon";
} else {
    themeIcon.className = "ti ti-sun";
}

themeBtn.addEventListener("click", function () {
    document.body.classList.toggle("light-theme");
    var isLight = document.body.classList.contains("light-theme");
    localStorage.setItem("theme", isLight ? "light" : "dark");
    themeIcon.className = isLight ? "ti ti-moon" : "ti ti-sun";
});

var popupOpen = false;

brandBtn.addEventListener("click", function (e) {
    e.stopPropagation();
    popupOpen = !popupOpen;
    brandPopup.classList.toggle("open", popupOpen);
});

document.addEventListener("click", function (e) {
    if (!brandPopup.contains(e.target) && !brandBtn.contains(e.target)) {
        popupOpen = false;
        brandPopup.classList.remove("open");
    }
});

function buildHero() {
    var hero = document.createElement("div");
    hero.className = "hero";
    hero.id = "heroSection";
    hero.innerHTML =
        '<img src="assets/logo.svg" alt="Alpha" class="hero-logo">' +
        '<h1>Alpha</h1>' +
        '<p>Your AI assistant for questions, ideas, research, learning, productivity, and everyday work.</p>' +
        '<div class="hero-chips">' +
        '<button class="chip" id="chip1">Summarize</button>' +
        '<button class="chip" id="chip2">Brainstorm</button>' +
        '<button class="chip" id="chip3">Write email</button>' +
        '<button class="chip" id="chip4">Explain</button>' +
        '</div>';
    return hero;
}

function attachChipListeners() {
    var chips = {
        chip1: 'Summarize something for me',
        chip2: 'Help me brainstorm ideas',
        chip3: 'Write a professional email',
        chip4: 'Explain a concept to me'
    };
    Object.keys(chips).forEach(function (id) {
        var el = document.getElementById(id);
        if (el) {
            el.addEventListener("click", function () {
                quickAsk(chips[id]);
            });
        }
    });
}

// Attach listeners to chips already in the HTML on page load
attachChipListeners();

refreshBtn.addEventListener("click", function () {
    chatBody.innerHTML = "";
    popupOpen = false;
    brandPopup.classList.remove("open");
    var hero = buildHero();
    chatBody.appendChild(hero);
    attachChipListeners();
});

function hideHero() {
    var hero = document.querySelector(".hero");
    if (hero) hero.remove();
}

function quickAsk(text) {
    questionInput.value = text;
    askAI();
}

// Make quickAsk globally accessible as a safety fallback
window.quickAsk = quickAsk;

function scrollToBottom() {
    chatBody.scrollTop = chatBody.scrollHeight;
}

function addMessage(text, type) {
    hideHero();

    var wrapper = document.createElement("div");
    wrapper.className = "message " + type;

    var avatarLabel = type === "bot" ? "A" : "U";

    wrapper.innerHTML =
        '<div class="msg-avatar">' + avatarLabel + '</div>' +
        '<div class="bubble">' + text + '</div>';

    chatBody.appendChild(wrapper);
    scrollToBottom();
}

function showTyping() {
    var typing = document.createElement("div");
    typing.className = "message bot";
    typing.id = "typing";
    typing.innerHTML =
        '<div class="msg-avatar">A</div>' +
        '<div class="bubble"><div class="typing-dots"><span></span><span></span><span></span></div></div>';
    chatBody.appendChild(typing);
    scrollToBottom();
}

function removeTyping() {
    var typing = document.getElementById("typing");
    if (typing) typing.remove();
}

async function askAI() {
    var question = questionInput.value.trim();
    if (!question) return;

    addMessage(question, "user");
    questionInput.value = "";
    questionInput.style.height = "auto";
    questionInput.style.height = questionInput.scrollHeight + "px";

    showTyping();
    sendBtn.disabled = true;

    try {
        var response = await fetch(API_URL, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ question: question })
        });

        removeTyping();

        if (!response.ok) {
            addMessage("Something went wrong. Please try again.", "bot");
            return;
        }

        var answer = await response.text();
        addMessage(answer, "bot");

    } catch (error) {
        removeTyping();
        addMessage("Unable to connect to Alpha. Check your connection.", "bot");
        console.error(error);

    } finally {
        sendBtn.disabled = false;
    }
}

sendBtn.addEventListener("click", function () {
    askAI();
});

questionInput.addEventListener("keydown", function (e) {
    if (e.key === "Enter" && !e.shiftKey) {
        e.preventDefault();
        askAI();
    }
});

questionInput.addEventListener("input", function () {
    this.style.height = "auto";
    this.style.height = Math.min(this.scrollHeight, 140) + "px";
});