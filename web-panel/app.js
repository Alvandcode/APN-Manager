const $=id=>document.getElementById(id);
let lang="fa";
const words={
 fa:{subtitle:"سازنده و مدیریت پروفایل‌های اینترنت",title:"پروفایل APN بساز و صادر کن",intro:"اطلاعات اپراتور را وارد کن، اعتبارسنجی کن و فایل قابل انتقال بساز.",details:"مشخصات پروفایل",profileName:"نام پروفایل",apnLabel:"APN",apnPh:"مقدار APN را از اپراتور بگیرید",mccLabel:"کد کشور (MCC)",mccPh:"۳ رقم",mncLabel:"کد شبکه (MNC)",mncPh:"۲ یا ۳ رقم",apnType:"نوع APN",protocol:"پروتکل",auth:"احراز هویت",proxy:"پروکسی (اختیاری)",port:"پورت (اختیاری)",download:"اعتبارسنجی و دانلود فایل",sample:"نمونه خالی",important:"نکته مهم",notice:"این فایل بسته اطلاعاتی APN Manager است، نه فایل نصب سیستمی اندروید. برای ثبت نهایی، اپلیکیشن یا تنظیمات گوشی لازم است. مقادیر اپراتور را از منبع رسمی تأیید کنید.",ok:"فایل پروفایل ساخته شد.",bad:"خطا: نام و APN الزامی هستند؛ MCC باید ۳ رقم و MNC باید ۲ یا ۳ رقم باشد؛ APN فقط حروف لاتین، عدد، نقطه، خط تیره یا زیرخط.",badPort:"خطا: پورت باید عددی بین ۱ تا ۶۵۵۳۵ باشد.",badProxy:"خطا: آدرس پروکسی فقط می‌تواند شامل حروف لاتین، عدد، نقطه یا خط تیره باشد."},
 en:{subtitle:"Mobile data profile builder",title:"Create and export an APN profile",intro:"Enter carrier settings, validate them and export a portable profile.",details:"Profile details",profileName:"Profile name",apnLabel:"APN",apnPh:"APN provided by carrier",mccLabel:"MCC",mccPh:"3 digits",mncLabel:"MNC",mncPh:"2–3 digits",apnType:"APN type",protocol:"Protocol",auth:"Authentication",proxy:"Proxy (optional)",port:"Port (optional)",download:"Validate and download",sample:"Clear sample",important:"Important",notice:"This is an APN Manager data package, not an Android system installer. Final registration requires the app or device settings. Verify carrier values with an official source.",ok:"Profile file created.",bad:"Error: name and APN are required; MCC must be 3 digits and MNC 2 or 3 digits; APN may only contain letters, digits, dots, dashes or underscores.",badPort:"Error: port must be a number between 1 and 65535.",badProxy:"Error: proxy may only contain letters, digits, dots or dashes."}
};
function applyLang(){
 document.documentElement.lang=lang;
 document.documentElement.dir=lang==="fa"?"rtl":"ltr";
 document.querySelectorAll("[data-i18n]").forEach(e=>e.textContent=words[lang][e.dataset.i18n]);
 document.querySelectorAll("[data-i18n-ph]").forEach(e=>e.placeholder=words[lang][e.dataset.i18nPh]);
 $("langBtn").textContent=lang==="fa"?"EN":"فا";
}
$("langBtn").onclick=()=>{lang=lang==="fa"?"en":"fa";applyLang();};
$("themeBtn").onclick=()=>document.body.classList.toggle("light");
$("sampleBtn").onclick=()=>{["name","apn","mcc","mnc","proxy","port"].forEach(id=>$(id).value="");$("apnType").value="default,supl";$("protocol").value="IPV4V6";$("authType").value="NONE";$("message").textContent="";};
$("profileForm").onsubmit=e=>{
 e.preventDefault();
 const v=id=>$(id).value.trim();
 const mcc=v("mcc"),mnc=v("mnc"),proxy=v("proxy"),port=v("port");
 if(!v("name")||!v("apn")||!/^[A-Za-z0-9._-]{1,100}$/.test(v("apn"))||(mcc&&!/^\d{3}$/.test(mcc))||(mnc&&!/^\d{2,3}$/.test(mnc))){$("message").textContent=words[lang].bad;return;}
 if(port&&(!/^\d{1,5}$/.test(port)||+port<1||+port>65535)){$("message").textContent=words[lang].badPort;return;}
 if(proxy&&!/^[A-Za-z0-9.-]{1,100}$/.test(proxy)){$("message").textContent=words[lang].badProxy;return;}
 const payload={format:"apnpack",version:1,profile:{name:v("name"),apn:v("apn"),mcc,mnc,apnType:v("apnType")||"default,supl",protocol:v("protocol"),authType:v("authType"),proxy,port}};
 const blob=new Blob([JSON.stringify(payload,null,2)],{type:"application/vnd.apnmanager.profile+json"});
 const url=URL.createObjectURL(blob),a=document.createElement("a");a.href=url;a.download=(v("name").replace(/[^\p{L}\p{N}_-]+/gu,"_")||"profile")+".apnpack";a.click();URL.revokeObjectURL(url);$("message").textContent=words[lang].ok;
};
applyLang();
