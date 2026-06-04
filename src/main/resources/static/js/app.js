const API_URL = "/api/ai/ask";

const questionInput = document.getElementById("question");
const sendBtn       = document.getElementById("sendBtn");
const chatBody      = document.getElementById("chatBody");
const themeBtn      = document.getElementById("themeBtn");
const themeIcon     = document.getElementById("themeIcon");
const refreshBtn    = document.getElementById("refreshBtn");
const brandBtn      = document.getElementById("brandBtn");
const brandPopup    = document.getElementById("brandPopup");
const heroSection   = document.getElementById("heroSection");

const savedTheme = localStorage.getItem("theme");

if (savedTheme === "light") {
    document.body.classList.add("light-theme");
    themeIcon.className = "ti ti-moon";
} else {
    themeIcon.className = "ti ti-sun";
}

themeBtn.addEventListener("click", () => {
    document.body.classList.toggle("light-theme");
    const isLight = document.body.classList.contains("light-theme");
    localStorage.setItem("theme", isLight ? "light" : "dark");
    themeIcon.className = isLight ? "ti ti-moon" : "ti ti-sun";
});


let popupOpen = false;

brandBtn.addEventListener("click", (e) => {
    e.stopPropagation();
    popupOpen = !popupOpen;
    brandPopup.classList.toggle("open", popupOpen);
});

document.addEventListener("click", (e) => {
    if (!brandPopup.contains(e.target) && !brandBtn.contains(e.target)) {
        popupOpen = false;
        brandPopup.classList.remove("open");
    }
});

refreshBtn.addEventListener("click", () => {
    chatBody.innerHTML = "";
    popupOpen = false;
    brandPopup.classList.remove("open");

    const hero = document.createElement("div");
    hero.className = "hero";
    hero.id = "heroSection";
    hero.innerHTML = `
        <img src="assets/logo.svg" alt="Alpha" class="hero-logo">
        <h1>Alpha</h1>
        <p>Your AI assistant for questions, ideas, research, learning, productivity, and everyday work.</p>
        <div class="hero-chips">
            <button class="chip" onclick="quickAsk('Summarize something for me')">Summarize</button>
            <button class="chip" onclick="quickAsk('Help me brainstorm ideas')">Brainstorm</button>
            <button class="chip" onclick="quickAsk('Write a professional email')">Write email</button>
            <button class="chip" onclick="quickAsk('Explain a concept to me')">Explain</button>
        </div>
    `;
    chatBody.appendChild(hero);
});

function hideHero() {
    const hero = document.querySelector(".hero");
    if (hero) hero.remove();
}

function quickAsk(text) {
    questionInput.value = text;
    askAI();
}

function addMessage(text, type) {
    hideHero();

    const wrapper = document.createElement("div");
    wrapper.className = `message ${type}`;

    const avatarLabel = type === "bot" ? "A" : "U";

    wrapper.innerHTML = `
        <div class="msg-avatar">${avatarLabel}</div>
        <div class="bubble">${text}</div>
    `;

    chatBody.appendChild(wrapper);
    chatBody.scrollTop = chatBody.scrollHeight;
}

function showTyping() {
    const typing = document.createElement("div");
    typing.className = "message bot";
    typing.id = "typing";
    typing.innerHTML = `
        <div class="msg-avatar">A</div>
        <div class="bubble">
            <div class="typing-dots">
                <span></span><span></span><span></span>
            </div>
        </div>
    `;
    chatBody.appendChild(typing);
    chatBody.scrollTop = chatBody.scrollHeight;
}

function removeTyping() {
    const typing = document.getElementById("typing");
    if (typing) typing.remove();
}

async function askAI() {
    const question = questionInput.value.trim();
    if (!question) return;

    addMessage(question, "user");
    questionInput.value = "";
    questionInput.style.height = "36px";

    showTyping();
    sendBtn.disabled = true;

    try {
        const response = await fetch(API_URL, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ question })
        });

        removeTyping();

        if (!response.ok) {
            addMessage("Something went wrong. Please try again.", "bot");
            return;
        }

        const answer = await response.text();
        addMessage(answer, "bot");

    } catch (error) {
        removeTyping();
        addMessage("Unable to connect to Alpha. Check your connection.", "bot");
        console.error(error);

    } finally {
        sendBtn.disabled = false;
    }
}

sendBtn.addEventListener("click", askAI);


questionInput.addEventListener("keydown", function (e) {
    if (e.key === "Enter" && !e.shiftKey) {
        e.preventDefault();
        askAI();
    }
});

questionInput.addEventListener("input", function () {
    this.style.height = "36px";
    this.style.height = this.scrollHeight + "px";
});