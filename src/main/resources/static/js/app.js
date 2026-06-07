//const API_URL = "/api/ai/ask";
//
//const questionInput = document.getElementById("question");
//const sendBtn       = document.getElementById("sendBtn");
//const chatBody      = document.getElementById("chatBody");
//const themeBtn      = document.getElementById("themeBtn");
//const themeIcon     = document.getElementById("themeIcon");
//const refreshBtn    = document.getElementById("refreshBtn");
//const brandBtn      = document.getElementById("brandBtn");
//const brandPopup    = document.getElementById("brandPopup");
//
//// ── Theme ──────────────────────────────────────────────────────────
//const savedTheme = localStorage.getItem("theme");
//if (savedTheme === "light") {
//    document.body.classList.add("light-theme");
//    themeIcon.className = "ti ti-moon";
//} else {
//    themeIcon.className = "ti ti-sun";
//}
//
//themeBtn.addEventListener("click", function () {
//    document.body.classList.toggle("light-theme");
//    var isLight = document.body.classList.contains("light-theme");
//    localStorage.setItem("theme", isLight ? "light" : "dark");
//    themeIcon.className = isLight ? "ti ti-moon" : "ti ti-sun";
//});
//
//// ── Brand popup ────────────────────────────────────────────────────
//var popupOpen = false;
//
//brandBtn.addEventListener("click", function (e) {
//    e.stopPropagation();
//    popupOpen = !popupOpen;
//    brandPopup.classList.toggle("open", popupOpen);
//});
//
//document.addEventListener("click", function (e) {
//    if (!brandPopup.contains(e.target) && !brandBtn.contains(e.target)) {
//        popupOpen = false;
//        brandPopup.classList.remove("open");
//    }
//});
//
//// ── Chip helpers ───────────────────────────────────────────────────
//var chipMap = {
//    chip1: "Summarize something for me",
//    chip2: "Help me brainstorm ideas",
//    chip3: "Write a professional email",
//    chip4: "Explain a concept to me"
//};
//
//function attachChipListeners() {
//    Object.keys(chipMap).forEach(function (id) {
//        var el = document.getElementById(id);
//        if (el) {
//            el.addEventListener("click", function () {
//                quickAsk(chipMap[id]);
//            });
//        }
//    });
//}
//
//attachChipListeners();
//
//// ── Refresh ────────────────────────────────────────────────────────
//refreshBtn.addEventListener("click", function () {
//    chatBody.innerHTML =
//        '<div class="hero" id="heroSection">' +
//        '<img src="assets/logo.svg" alt="Alpha" class="hero-logo">' +
//        '<h1>Alpha</h1>' +
//        '<p>Your AI assistant for questions, ideas, research, learning, productivity, and everyday work.</p>' +
//        '<div class="hero-chips">' +
//        '<button class="chip" id="chip1">Summarize</button>' +
//        '<button class="chip" id="chip2">Brainstorm</button>' +
//        '<button class="chip" id="chip3">Write email</button>' +
//        '<button class="chip" id="chip4">Explain</button>' +
//        '</div></div>';
//    popupOpen = false;
//    brandPopup.classList.remove("open");
//    attachChipListeners();
//});
//
//// ── Utilities ──────────────────────────────────────────────────────
//function hideHero() {
//    var hero = document.querySelector(".hero");
//    if (hero) hero.remove();
//}
//
//function quickAsk(text) {
//    questionInput.value = text;
//    askAI();
//}
//window.quickAsk = quickAsk;
//
//function scrollToBottom() {
//    chatBody.scrollTop = chatBody.scrollHeight;
//}
//
//// ── Messages ───────────────────────────────────────────────────────
//function addMessage(text, type) {
//    hideHero();
//    var wrapper = document.createElement("div");
//    wrapper.className = "message " + type;
//    wrapper.innerHTML =
//        '<div class="msg-avatar">' + (type === "bot" ? "A" : "U") + '</div>' +
//        '<div class="bubble">' + text + '</div>';
//    chatBody.appendChild(wrapper);
//    scrollToBottom();
//}
//
//function showTyping() {
//    var typing = document.createElement("div");
//    typing.className = "message bot";
//    typing.id = "typing";
//    typing.innerHTML =
//        '<div class="msg-avatar">A</div>' +
//        '<div class="bubble"><div class="typing-dots"><span></span><span></span><span></span></div></div>';
//    chatBody.appendChild(typing);
//    scrollToBottom();
//}
//
//function removeTyping() {
//    var el = document.getElementById("typing");
//    if (el) el.remove();
//}
//
//// ── Send ───────────────────────────────────────────────────────────
//async function askAI() {
//    var question = questionInput.value.trim();
//    if (!question) return;
//
//    addMessage(question, "user");
//    questionInput.value = "";
//    questionInput.style.height = "auto";
//
//    showTyping();
//    sendBtn.disabled = true;
//
//    try {
//        var response = await fetch(API_URL, {
//            method: "POST",
//            headers: { "Content-Type": "application/json" },
//            body: JSON.stringify({ question: question })
//        });
//        removeTyping();
//        if (!response.ok) { addMessage("Something went wrong. Please try again.", "bot"); return; }
//        var answer = await response.text();
//        addMessage(answer, "bot");
//    } catch (err) {
//        removeTyping();
//        addMessage("Unable to connect to Alpha. Check your connection.", "bot");
//        console.error(err);
//    } finally {
//        sendBtn.disabled = false;
//    }
//}
//
//sendBtn.addEventListener("click", function () { askAI(); });
//
//questionInput.addEventListener("keydown", function (e) {
//    if (e.key === "Enter" && !e.shiftKey) { e.preventDefault(); askAI(); }
//});
//
//questionInput.addEventListener("input", function () {
//    this.style.height = "auto";
//    this.style.height = Math.min(this.scrollHeight, 140) + "px";
//});