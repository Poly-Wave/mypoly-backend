-- Flyway seed: Terms 기본 데이터 (Notion export HTML 기반)
SET search_path TO user_service;

-- 서비스 이용 약관 (version=1, required=true)
INSERT INTO terms (name, title, content, version, required, effective_from)
VALUES (
  'TERMS_OF_SERVICE',
  '서비스 이용 약관',
  $terms_of_service$
<style>
/* cspell:disable-file */
/* webkit printing magic: print all background colors */
html {
	-webkit-print-color-adjust: exact;
}
* {
	box-sizing: border-box;
	-webkit-print-color-adjust: exact;
}

html,
body {
	margin: 0;
	padding: 0;
}
@media only screen {
	body {
		margin: 2em auto;
		max-width: 900px;
		color: rgb(55, 53, 47);
	}
}

body {
	line-height: 1.5;
	white-space: pre-wrap;
}

a,
a.visited {
	color: inherit;
	text-decoration: underline;
}

.pdf-relative-link-path {
	font-size: 80%;
	color: #444;
}

h1,
h2,
h3 {
	letter-spacing: -0.01em;
	line-height: 1.2;
	font-weight: 600;
	margin-bottom: 0;
}

/* Override strong tags inside headings to maintain consistent weight */
h1 strong,
h2 strong,
h3 strong {
	font-weight: 600;
}

.page-title {
	font-size: 2.5rem;
	font-weight: 700;
	margin-top: 0;
	margin-bottom: 0.75em;
}

h1 {
	font-size: 1.875rem;
	margin-top: 1.875rem;
}

h2 {
	font-size: 1.5rem;
	margin-top: 1.5rem;
}

h3 {
	font-size: 1.25rem;
	margin-top: 1.25rem;
}

.source {
	border: 1px solid #ddd;
	border-radius: 3px;
	padding: 1.5em;
	word-break: break-all;
}

.callout {
	border-radius: 10px;
	padding: 1rem;
}

figure {
	margin: 1.25em 0;
	page-break-inside: avoid;
}

figcaption {
	opacity: 0.5;
	font-size: 85%;
	margin-top: 0.5em;
}

mark {
	background-color: transparent;
}

.indented {
	padding-left: 1.5em;
}

hr {
	background: transparent;
	display: block;
	width: 100%;
	height: 1px;
	visibility: visible;
	border: none;
	border-bottom: 1px solid rgba(55, 53, 47, 0.09);
}

img {
	max-width: 100%;
}

@media only print {
	img {
		max-height: 100vh;
		object-fit: contain;
	}
}

@page {
	margin: 1in;
}

.collection-content {
	font-size: 0.875rem;
}

.collection-content td {
	white-space: pre-wrap;
	word-break: break-word;
}

.column-list {
	display: flex;
	justify-content: space-between;
}

.column {
	padding: 0 1em;
}

.column:first-child {
	padding-left: 0;
}

.column:last-child {
	padding-right: 0;
}

.table_of_contents-item {
	display: block;
	font-size: 0.875rem;
	line-height: 1.3;
	padding: 0.125rem;
}

.table_of_contents-indent-1 {
	margin-left: 1.5rem;
}

.table_of_contents-indent-2 {
	margin-left: 3rem;
}

.table_of_contents-indent-3 {
	margin-left: 4.5rem;
}

.table_of_contents-link {
	text-decoration: none;
	opacity: 0.7;
	border-bottom: 1px solid rgba(55, 53, 47, 0.18);
}

table,
th,
td {
	border: 1px solid rgba(55, 53, 47, 0.09);
	border-collapse: collapse;
}

table {
	border-left: none;
	border-right: none;
}

th,
td {
	font-weight: normal;
	padding: 0.25em 0.5em;
	line-height: 1.5;
	min-height: 1.5em;
	text-align: left;
}

th {
	color: rgba(55, 53, 47, 0.6);
}

ol,
ul {
	margin: 0;
	margin-block-start: 0.6em;
	margin-block-end: 0.6em;
}

li > ol:first-child,
li > ul:first-child {
	margin-block-start: 0.6em;
}

ul > li {
	list-style: disc;
}

ul.to-do-list {
	padding-inline-start: 0;
}

ul.to-do-list > li {
	list-style: none;
}

.to-do-children-checked {
	text-decoration: line-through;
	opacity: 0.375;
}

ul.toggle > li {
	list-style: none;
}

ul {
	padding-inline-start: 1.7em;
}

ul > li {
	padding-left: 0.1em;
}

ol {
	padding-inline-start: 1.6em;
}

ol > li {
	padding-left: 0.2em;
}

.mono ol {
	padding-inline-start: 2em;
}

.mono ol > li {
	text-indent: -0.4em;
}

.toggle {
	padding-inline-start: 0em;
	list-style-type: none;
}

/* Indent toggle children */
.toggle > li > details {
	padding-left: 1.7em;
}

.toggle > li > details > summary {
	margin-left: -1.1em;
}

.selected-value {
	display: inline-block;
	padding: 0 0.5em;
	background: rgba(206, 205, 202, 0.5);
	border-radius: 3px;
	margin-right: 0.5em;
	margin-top: 0.3em;
	margin-bottom: 0.3em;
	white-space: nowrap;
}

.collection-title {
	display: inline-block;
	margin-right: 1em;
}

.page-description {
	margin-bottom: 2em;
}

.simple-table {
	margin-top: 1em;
	font-size: 0.875rem;
	empty-cells: show;
}
.simple-table td {
	height: 29px;
	min-width: 120px;
}

.simple-table th {
	height: 29px;
	min-width: 120px;
}

.simple-table-header-color {
	background: rgb(247, 246, 243);
	color: black;
}
.simple-table-header {
	font-weight: 500;
}

time {
	opacity: 0.5;
}

.icon {
	display: inline-flex;
	align-items: center;
	justify-content: center;
	max-width: 1.2em;
	max-height: 1.2em;
	text-decoration: none;
	vertical-align: text-bottom;
	margin-right: 0.5em;
}

img.icon {
	border-radius: 3px;
}

.callout img.notion-static-icon {
	width: 1em;
	height: 1em;
}

.callout p {
	margin: 0;
}

.callout h1,
.callout h2,
.callout h3 {
	margin: 0 0 0.6rem;
}

.user-icon {
	width: 1.5em;
	height: 1.5em;
	border-radius: 100%;
	margin-right: 0.5rem;
}

.user-icon-inner {
	font-size: 0.8em;
}

.text-icon {
	border: 1px solid #000;
	text-align: center;
}

.page-cover-image {
	display: block;
	object-fit: cover;
	width: 100%;
	max-height: 30vh;
}

.page-header-icon {
	font-size: 3rem;
	margin-bottom: 1rem;
}

.page-header-icon-with-cover {
	margin-top: -0.72em;
	margin-left: 0.07em;
}

.page-header-icon img {
	border-radius: 3px;
}

.link-to-page {
	margin: 1em 0;
	padding: 0;
	border: none;
	font-weight: 500;
}

p > .user {
	opacity: 0.5;
}

td > .user,
td > time {
	white-space: nowrap;
}

input[type="checkbox"] {
	transform: scale(1.5);
	margin-right: 0.6em;
	vertical-align: middle;
}

p {
	margin-top: 0.5em;
	margin-bottom: 0.5em;
}

.image {
	border: none;
	margin: 1.5em 0;
	padding: 0;
	border-radius: 0;
	text-align: center;
}

.code,
code {
	background: rgba(135, 131, 120, 0.15);
	border-radius: 3px;
	padding: 0.2em 0.4em;
	border-radius: 3px;
	font-size: 85%;
	tab-size: 2;
}

code {
	color: #eb5757;
}

.code {
	padding: 1.5em 1em;
}

.code-wrap {
	white-space: pre-wrap;
	word-break: break-all;
}

.code > code {
	background: none;
	padding: 0;
	font-size: 100%;
	color: inherit;
}

blockquote {
	font-size: 1em;
	margin: 1em 0;
	padding-left: 1em;
	border-left: 3px solid rgb(55, 53, 47);
}

blockquote.quote-large {
	font-size: 1.25em;
}

.bookmark {
	text-decoration: none;
	max-height: 8em;
	padding: 0;
	display: flex;
	width: 100%;
	align-items: stretch;
}

.bookmark-title {
	font-size: 0.85em;
	overflow: hidden;
	text-overflow: ellipsis;
	height: 1.75em;
	white-space: nowrap;
}

.bookmark-text {
	display: flex;
	flex-direction: column;
}

.bookmark-info {
	flex: 4 1 180px;
	padding: 12px 14px 14px;
	display: flex;
	flex-direction: column;
	justify-content: space-between;
}

.bookmark-image {
	width: 33%;
	flex: 1 1 180px;
	display: block;
	position: relative;
	object-fit: cover;
	border-radius: 1px;
}

.bookmark-description {
	color: rgba(55, 53, 47, 0.6);
	font-size: 0.75em;
	overflow: hidden;
	max-height: 4.5em;
	word-break: break-word;
}

.bookmark-href {
	font-size: 0.75em;
	margin-top: 0.25em;
}

.sans { font-family: ui-sans-serif, -apple-system, BlinkMacSystemFont, "Segoe UI Variable Display", "Segoe UI", Helvetica, "Apple Color Emoji", "Noto Sans Arabic", "Noto Sans Hebrew", Arial, sans-serif, "Segoe UI Emoji", "Segoe UI Symbol"; }
.code { font-family: "SFMono-Regular", Menlo, Consolas, "PT Mono", "Liberation Mono", Courier, monospace; }
.serif { font-family: Lyon-Text, Georgia, ui-serif, serif; }
.mono { font-family: iawriter-mono, Nitti, Menlo, Courier, monospace; }
.pdf .sans { font-family: Inter, ui-sans-serif, -apple-system, BlinkMacSystemFont, "Segoe UI Variable Display", "Segoe UI", Helvetica, "Apple Color Emoji", "Noto Sans Arabic", "Noto Sans Hebrew", Arial, sans-serif, "Segoe UI Emoji", "Segoe UI Symbol", 'Twemoji', 'Noto Color Emoji', 'Noto Sans CJK JP'; }
.pdf:lang(zh-CN) .sans { font-family: Inter, ui-sans-serif, -apple-system, BlinkMacSystemFont, "Segoe UI Variable Display", "Segoe UI", Helvetica, "Apple Color Emoji", "Noto Sans Arabic", "Noto Sans Hebrew", Arial, sans-serif, "Segoe UI Emoji", "Segoe UI Symbol", 'Twemoji', 'Noto Color Emoji', 'Noto Sans CJK SC'; }
.pdf:lang(zh-TW) .sans { font-family: Inter, ui-sans-serif, -apple-system, BlinkMacSystemFont, "Segoe UI Variable Display", "Segoe UI", Helvetica, "Apple Color Emoji", "Noto Sans Arabic", "Noto Sans Hebrew", Arial, sans-serif, "Segoe UI Emoji", "Segoe UI Symbol", 'Twemoji', 'Noto Color Emoji', 'Noto Sans CJK TC'; }
.pdf:lang(ko-KR) .sans { font-family: Inter, ui-sans-serif, -apple-system, BlinkMacSystemFont, "Segoe UI Variable Display", "Segoe UI", Helvetica, "Apple Color Emoji", "Noto Sans Arabic", "Noto Sans Hebrew", Arial, sans-serif, "Segoe UI Emoji", "Segoe UI Symbol", 'Twemoji', 'Noto Color Emoji', 'Noto Sans CJK KR'; }
.pdf .code { font-family: Source Code Pro, "SFMono-Regular", Menlo, Consolas, "PT Mono", "Liberation Mono", Courier, monospace, 'Twemoji', 'Noto Color Emoji', 'Noto Sans Mono CJK JP'; }
.pdf:lang(zh-CN) .code { font-family: Source Code Pro, "SFMono-Regular", Menlo, Consolas, "PT Mono", "Liberation Mono", Courier, monospace, 'Twemoji', 'Noto Color Emoji', 'Noto Sans Mono CJK SC'; }
.pdf:lang(zh-TW) .code { font-family: Source Code Pro, "SFMono-Regular", Menlo, Consolas, "PT Mono", "Liberation Mono", Courier, monospace, 'Twemoji', 'Noto Color Emoji', 'Noto Sans Mono CJK TC'; }
.pdf:lang(ko-KR) .code { font-family: Source Code Pro, "SFMono-Regular", Menlo, Consolas, "PT Mono", "Liberation Mono", Courier, monospace, 'Twemoji', 'Noto Color Emoji', 'Noto Sans Mono CJK KR'; }
.pdf .serif { font-family: PT Serif, Lyon-Text, Georgia, ui-serif, serif, 'Twemoji', 'Noto Color Emoji', 'Noto Serif CJK JP'; }
.pdf:lang(zh-CN) .serif { font-family: PT Serif, Lyon-Text, Georgia, ui-serif, serif, 'Twemoji', 'Noto Color Emoji', 'Noto Serif CJK SC'; }
.pdf:lang(zh-TW) .serif { font-family: PT Serif, Lyon-Text, Georgia, ui-serif, serif, 'Twemoji', 'Noto Color Emoji', 'Noto Serif CJK TC'; }
.pdf:lang(ko-KR) .serif { font-family: PT Serif, Lyon-Text, Georgia, ui-serif, serif, 'Twemoji', 'Noto Color Emoji', 'Noto Serif CJK KR'; }
.pdf .mono { font-family: PT Mono, iawriter-mono, Nitti, Menlo, Courier, monospace, 'Twemoji', 'Noto Color Emoji', 'Noto Sans Mono CJK JP'; }
.pdf:lang(zh-CN) .mono { font-family: PT Mono, iawriter-mono, Nitti, Menlo, Courier, monospace, 'Twemoji', 'Noto Color Emoji', 'Noto Sans Mono CJK SC'; }
.pdf:lang(zh-TW) .mono { font-family: PT Mono, iawriter-mono, Nitti, Menlo, Courier, monospace, 'Twemoji', 'Noto Color Emoji', 'Noto Sans Mono CJK TC'; }
.pdf:lang(ko-KR) .mono { font-family: PT Mono, iawriter-mono, Nitti, Menlo, Courier, monospace, 'Twemoji', 'Noto Color Emoji', 'Noto Sans Mono CJK KR'; }
.highlight-default {
	color: rgba(44, 44, 43, 1);
}
.highlight-gray {
	color: rgba(125, 122, 117, 1);
	fill: rgba(125, 122, 117, 1);
}
.highlight-brown {
	color: rgba(159, 118, 90, 1);
	fill: rgba(159, 118, 90, 1);
}
.highlight-orange {
	color: rgba(210, 123, 45, 1);
	fill: rgba(210, 123, 45, 1);
}
.highlight-yellow {
	color: rgba(203, 148, 52, 1);
	fill: rgba(203, 148, 52, 1);
}
.highlight-teal {
	color: rgba(80, 148, 110, 1);
	fill: rgba(80, 148, 110, 1);
}
.highlight-blue {
	color: rgba(56, 125, 201, 1);
	fill: rgba(56, 125, 201, 1);
}
.highlight-purple {
	color: rgba(154, 107, 180, 1);
	fill: rgba(154, 107, 180, 1);
}
.highlight-pink {
	color: rgba(193, 76, 138, 1);
	fill: rgba(193, 76, 138, 1);
}
.highlight-red {
	color: rgba(207, 81, 72, 1);
	fill: rgba(207, 81, 72, 1);
}
.highlight-default_background {
	color: rgba(44, 44, 43, 1);
}
.highlight-gray_background {
	background: rgba(42, 28, 0, 0.07);
}
.highlight-brown_background {
	background: rgba(139, 46, 0, 0.086);
}
.highlight-orange_background {
	background: rgba(224, 101, 1, 0.129);
}
.highlight-yellow_background {
	background: rgba(211, 168, 0, 0.137);
}
.highlight-teal_background {
	background: rgba(0, 100, 45, 0.09);
}
.highlight-blue_background {
	background: rgba(0, 124, 215, 0.094);
}
.highlight-purple_background {
	background: rgba(102, 0, 178, 0.078);
}
.highlight-pink_background {
	background: rgba(197, 0, 93, 0.086);
}
.highlight-red_background {
	background: rgba(223, 22, 0, 0.094);
}
.block-color-default {
	color: inherit;
	fill: inherit;
}
.block-color-gray {
	color: rgba(125, 122, 117, 1);
	fill: rgba(125, 122, 117, 1);
}
.block-color-brown {
	color: rgba(159, 118, 90, 1);
	fill: rgba(159, 118, 90, 1);
}
.block-color-orange {
	color: rgba(210, 123, 45, 1);
	fill: rgba(210, 123, 45, 1);
}
.block-color-yellow {
	color: rgba(203, 148, 52, 1);
	fill: rgba(203, 148, 52, 1);
}
.block-color-teal {
	color: rgba(80, 148, 110, 1);
	fill: rgba(80, 148, 110, 1);
}
.block-color-blue {
	color: rgba(56, 125, 201, 1);
	fill: rgba(56, 125, 201, 1);
}
.block-color-purple {
	color: rgba(154, 107, 180, 1);
	fill: rgba(154, 107, 180, 1);
}
.block-color-pink {
	color: rgba(193, 76, 138, 1);
	fill: rgba(193, 76, 138, 1);
}
.block-color-red {
	color: rgba(207, 81, 72, 1);
	fill: rgba(207, 81, 72, 1);
}
.block-color-default_background {
	color: inherit;
	fill: inherit;
}
.block-color-gray_background {
	background: rgba(240, 239, 237, 1);
}
.block-color-brown_background {
	background: rgba(245, 237, 233, 1);
}
.block-color-orange_background {
	background: rgba(251, 235, 222, 1);
}
.block-color-yellow_background {
	background: rgba(249, 243, 220, 1);
}
.block-color-teal_background {
	background: rgba(232, 241, 236, 1);
}
.block-color-blue_background {
	background: rgba(229, 242, 252, 1);
}
.block-color-purple_background {
	background: rgba(243, 235, 249, 1);
}
.block-color-pink_background {
	background: rgba(250, 233, 241, 1);
}
.block-color-red_background {
	background: rgba(252, 233, 231, 1);
}
.select-value-color-default { background-color: rgba(42, 28, 0, 0.07); }
.select-value-color-gray { background-color: rgba(28, 19, 1, 0.11); }
.select-value-color-brown { background-color: rgba(127, 51, 0, 0.156); }
.select-value-color-orange { background-color: rgba(196, 88, 0, 0.203); }
.select-value-color-yellow { background-color: rgba(209, 156, 0, 0.282); }
.select-value-color-green { background-color: rgba(0, 96, 38, 0.156); }
.select-value-color-blue { background-color: rgba(0, 118, 217, 0.203); }
.select-value-color-purple { background-color: rgba(92, 0, 163, 0.141); }
.select-value-color-pink { background-color: rgba(183, 0, 78, 0.152); }
.select-value-color-red { background-color: rgba(206, 24, 0, 0.164); }

.checkbox {
	display: inline-flex;
	vertical-align: text-bottom;
	width: 16;
	height: 16;
	background-size: 16px;
	margin-left: 2px;
	margin-right: 5px;
}

.checkbox-on {
	background-image: url("data:image/svg+xml;charset=UTF-8,%3Csvg%20width%3D%2216%22%20height%3D%2216%22%20viewBox%3D%220%200%2016%2016%22%20fill%3D%22none%22%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%3E%0A%3Crect%20width%3D%2216%22%20height%3D%2216%22%20fill%3D%22%2358A9D7%22%2F%3E%0A%3Cpath%20d%3D%22M6.71429%2012.2852L14%204.9995L12.7143%203.71436L6.71429%209.71378L3.28571%206.2831L2%207.57092L6.71429%2012.2852Z%22%20fill%3D%22white%22%2F%3E%0A%3C%2Fsvg%3E");
}

.checkbox-off {
	background-image: url("data:image/svg+xml;charset=UTF-8,%3Csvg%20width%3D%2216%22%20height%3D%2216%22%20viewBox%3D%220%200%2016%2016%22%20fill%3D%22none%22%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%3E%0A%3Crect%20x%3D%220.75%22%20y%3D%220.75%22%20width%3D%2214.5%22%20height%3D%2214.5%22%20fill%3D%22white%22%20stroke%3D%22%2336352F%22%20stroke-width%3D%221.5%22%2F%3E%0A%3C%2Fsvg%3E");
}
	
</style>
<article class="page sans">
<div class="page-body"><div dir="auto" style="display:contents"><h3 class="" id="28dcbc69-4770-80fb-8827-ffa568127f6f"><strong>제1조 (목적)</strong></h3></div><div dir="auto" style="display:contents"><p class="" id="28dcbc69-4770-80fb-be90-f9e313073bf5">마이폴리(이하 ‘회사’)가 제공하는 서비스를 이용해 주셔서 감사합니다. 본 약관은 회사가 제공하는 서비스의 이용과 관련하여 회사와 회원과의 권리, 의무 및 책임사항, 기타 필요한 사항을 규정함을 목적으로 합니다.</p></div><div dir="auto" style="display:contents"><p class="" id="28dcbc69-4770-807b-b85a-c021430605fc">
</p></div><div dir="auto" style="display:contents"><h3 class="" id="28dcbc69-4770-805b-8576-c0382ec8ee3d"><strong>제2조 (정의)</strong></h3></div><div dir="auto" style="display:contents"><ol class="numbered-list" id="28dcbc69-4770-80d6-ba68-ee224fad0558" start="1" type="1"><li>“회원”이란 회사가 제공하는 서비스를 통해 본 약관에 따라 회사의 이용절차에 동의하고 회사가 제공하는 서비스를 이용하는 이용자를 말합니다.</li></ol></div><div dir="auto" style="display:contents"><ol class="numbered-list" id="28dcbc69-4770-804b-a423-f355cab1817b" start="2" type="1"><li>"콘텐츠"라 함은 회사가 제공하는 디지털 콘텐츠 및 기타 관련 정보 (부호·문자·음성·음향·이미지, 영상 등으로 표현된 자료 또는 정보)를 말합니다.</li></ol></div><div dir="auto" style="display:contents"><ol class="numbered-list" id="28dcbc69-4770-801b-bcfe-db41dfa946f8" start="3" type="1"><li>“서비스"란 회원이 이용할 수 있는 회사 및 회사 관련 제반 서비스를 의미합니다.</li></ol></div><div dir="auto" style="display:contents"><p class="" id="28dcbc69-4770-80e3-9456-dd8e744c1b5b">
</p></div><div dir="auto" style="display:contents"><h3 class="" id="28dcbc69-4770-80a6-b076-edb69ff99262"><strong>제3조 (회원가입)</strong></h3></div><div dir="auto" style="display:contents"><ol class="numbered-list" id="28dcbc69-4770-80b9-9a1a-f9ba665dd599" start="1" type="1"><li>회원이 되고자 하는 자는 회사가 정한 가입 양식에 따라 회원정보를 기입·제공하고 "동의", "확인" 등의 버튼을 누르는 방법으로 회원 가입을 신청합니다.</li></ol></div><div dir="auto" style="display:contents"><ol class="numbered-list" id="28dcbc69-4770-8079-9763-cb1741c8d91c" start="2" type="1"><li>회사는 제1항과 같이 회원으로 가입할 것을 신청한 자가 다음 각 호에 해당하지 않는 한 신청한 자를 회원으로 등록합니다.<div dir="auto" style="display:contents"><ul class="bulleted-list" id="28dcbc69-4770-80f1-8ef6-fdf5ba9cf623"><li style="list-style-type:disc"> 등록 내용에 허위, 기재 누락, 오기가 있는 경우</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28dcbc69-4770-80ee-bbec-f8f6d86706a5"><li style="list-style-type:disc"> 제6조 제3항에 해당하는 회원 자격 제한 및 정지, 상실을 한 경험이 있었던 경우</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28dcbc69-4770-805b-b3da-da54e1a0a3d5"><li style="list-style-type:disc"> 기타 회원으로 등록하는 것이 회사의 서비스 운영 및 기술상 현저히 지장이 있다고 판단되는 경우</li></ul></div></li></ol></div><div dir="auto" style="display:contents"><ol class="numbered-list" id="28dcbc69-4770-80f2-b74b-fd1007ac3945" start="3" type="1"><li>회원가입계약의 성립 시기는 회사의 승낙이 가입신청자에게 도달한 시점으로 합니다.</li></ol></div><div dir="auto" style="display:contents"><ol class="numbered-list" id="28dcbc69-4770-80eb-a264-ecb48aeb21aa" start="4" type="1"><li>회원은 제1항의 회원정보 기재 내용에 변경이 발생한 경우, 즉시 변경사항을 정정하여 기재하여야 합니다.</li></ol></div><div dir="auto" style="display:contents"><p class="" id="28dcbc69-4770-804d-be49-dd513c2eba67">
</p></div><div dir="auto" style="display:contents"><h3 class="" id="28dcbc69-4770-80dd-ab75-f0bde53de4db"><strong>제4조 (서비스의 제공 및 변경)</strong></h3></div><div dir="auto" style="display:contents"><ol class="numbered-list" id="28dcbc69-4770-8057-a8f0-d3daed3b01d1" start="1" type="1"><li>회사는 회원에게 아래와 같은 서비스를 제공합니다.<div dir="auto" style="display:contents"><ul class="bulleted-list" id="28dcbc69-4770-80e2-aa6a-c61fa47e5768"><li style="list-style-type:disc">정보 및 콘텐츠 제공 서비스</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28dcbc69-4770-80a8-9b45-eb187c4dc74e"><li style="list-style-type:disc">콘텐츠 추천 서비스</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28dcbc69-4770-802b-9cad-db6cb8747598"><li style="list-style-type:disc">찬성, 반대 투표 및 토론 커뮤니티 서비스<div dir="auto" style="display:contents"><ul class="bulleted-list" id="28dcbc69-4770-806c-9224-ea56dd53a9a2"><li style="list-style-type:circle">회사는 투표의 공정성을 확보하고 과도한 분쟁 및 쏠림 현상을 막기 위해 <span style="border-bottom:0.05em solid">선투표 시스템</span>을 적용하고 있습니다.  단, 법률에 의한 정보 공개 요구가 있는 경우에는 예외로 합니다.</li></ul></div><div dir="auto" style="display:contents"><p class="" id="28dcbc69-4770-8072-a84d-c5216a301131">* 선투표 시스템</p></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28dcbc69-4770-8061-b4ec-e0a2ad6c415a"><li style="list-style-type:circle">투표 결과 : 사용자는 <strong>투표 후</strong> 투표 결과를 확인할 수 있습니다.</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28dcbc69-4770-805f-beb7-ceb7485b28a1"><li style="list-style-type:circle">댓글 : 사용자는 <strong>투표 후</strong>  투표한 찬반 탭에 한해 의견 댓글이 가능합니다.</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28dcbc69-4770-806d-8273-c68b3cf338a3"><li style="list-style-type:circle">답글 : 사용자는 <strong>투표 후</strong>  찬반 탭 모두에 답글 작성이 가능합니다.</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28dcbc69-4770-8087-bc08-ee0f007b96e1"><li style="list-style-type:circle">공감 개수  : 사용자는 <strong>투표 후 댓글</strong>의 공감 개수 확인이 가능합니다.</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28dcbc69-4770-8088-9607-c5568bfec2f3"><li style="list-style-type:circle">댓글 개수  : 사용자는 <strong>투표 후 댓글</strong>의 개수 확인이 가능합니다.</li></ul></div></li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28dcbc69-4770-8019-b513-d6c06fb938c0"><li style="list-style-type:disc">기타 회사가 자체 개발하거나 다른 회사와의 협력계약 등을 통해 회원들에게 제공할 일체의 서비스</li></ul></div></li></ol></div><div dir="auto" style="display:contents"><ol class="numbered-list" id="28dcbc69-4770-80ee-98af-fde6c73cb6f1" start="2" type="1"><li>회사는 서비스의 내용 및 제공일자를 제7조 제2항에서 정한 방법으로 회원에게 통지하고, 제1항에 정한 서비스를 변경하여 제공할 수 있습니다.</li></ol></div><div dir="auto" style="display:contents"><ol class="numbered-list" id="28dcbc69-4770-8031-b4c6-fd814f104411" start="3" type="1"><li>회사는 더 나은 서비스 제공을 위하여 서비스에 필요한 소프트웨어의 업데이트 버전을 제공할 수 있습니다. 소프트웨어의 업데이트에는 중요한 기능의 추가 또는 불필요한 기능의 제거 등이 포함되어 있습니다.</li></ol></div><div dir="auto" style="display:contents"><p class="" id="28dcbc69-4770-80c3-b9d2-da48b8debb3c">
</p></div><div dir="auto" style="display:contents"><h3 class="" id="28dcbc69-4770-80b7-a68a-e1dfa3ed86ad"><strong>제5조 (서비스의 중단)</strong></h3></div><div dir="auto" style="display:contents"><ol class="numbered-list" id="28dcbc69-4770-806f-9bcb-db7446eb838c" start="1" type="1"><li>회사는 컴퓨터 등 정보통신설비의 보수점검·교체 및 고장, 통신의 두절 등의 사유가 발생한 경우에는 서비스의 제공을 일시적으로 중단할 수 있고, 새로운 서비스로의 교체, 기타 회사가 적절하다고 판단하는 사유에 기하여 현재 제공되는 서비스를 완전히 중단할 수 있습니다.</li></ol></div><div dir="auto" style="display:contents"><ol class="numbered-list" id="28dcbc69-4770-80e3-8f8a-fcb7cf5f17d4" start="2" type="1"><li>제1항에 의한 서비스 중단의 경우에 회사는 제7조 제2항에서 정한 방법으로 회원에게 통지합니다. 다만, 회사가 통제할 수 없는 사유로 인한 서비스의 중단(시스템 관리자의 고의, 과실이 없는 디스크 장애, 시스템 다운 등)으로 인하여 사전 통지가 불가능한 경우에는 그러하지 아니합니다.</li></ol></div><div dir="auto" style="display:contents"><p class="" id="28dcbc69-4770-808b-8678-d9d0cf0febae">
</p></div><div dir="auto" style="display:contents"><h3 class="" id="28dcbc69-4770-8031-b040-d6add5437bd0"><strong>제6조 (회원 탈퇴 및 자격 상실 등)</strong></h3></div><div dir="auto" style="display:contents"><ol class="numbered-list" id="28dcbc69-4770-80fa-85cc-d11337a25587" start="1" type="1"><li>회원은 사에 언제든지 자신의 회원 등록 말소(회원 탈퇴)를 요청할 수 있으며 회사는 위 요청을 받은 즉시 해당 회원의 회원 등록 말소를 위한 절차를 밟습니다.</li></ol></div><div dir="auto" style="display:contents"><ol class="numbered-list" id="28dcbc69-4770-8072-89b0-ef5b319cf0bd" start="2" type="1"><li>회원 탈퇴가 이루어진 경우 사용자가 작성한 게시물의 작성자는 “탈퇴한 사용자”로 표시 됩니다. 작성한 게시물의 삭제를 원하시는 경우, 회원탈퇴 전 개별 삭제하여야 합니다.</li></ol></div><div dir="auto" style="display:contents"><ol class="numbered-list" id="28dcbc69-4770-8004-839f-e3556ce63717" start="3" type="1"><li>"회원"의 요청으로 인한 회원탈퇴 시 1개월간 재가입이 제한됩니다. 단, 회사와 "회원"간에 사전 협의한 경우에는 재가입이 가능합니다.</li></ol></div><div dir="auto" style="display:contents"><ol class="numbered-list" id="28dcbc69-4770-8085-ab23-f3d37db39e94" start="4" type="1"><li>회원이 다음 각 호의 사유에 해당하는 경우, 회사는 회원의 회원자격을 적절한 방법으로 제한 및 정지, 상실시킬 수 있습니다.<div dir="auto" style="display:contents"><ul class="bulleted-list" id="28dcbc69-4770-80b7-ac1f-c67a44a80d5c"><li style="list-style-type:disc"> 가입 신청 시에 허위 내용을 등록한 경우</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28dcbc69-4770-80c2-90b7-e1208c2fd393"><li style="list-style-type:disc"> 다른 사람의 서비스 이용을 방해하거나 그 정보를 도용하는 등 전자거래질서를 위협하는 경우</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28dcbc69-4770-80e1-8c42-f6a15d6033c0"><li style="list-style-type:disc"> 서비스를 이용하여 법령과 본 약관이 금지하거나 공서양속에 반하는 행위를 하는 경우</li></ul></div></li></ol></div><div dir="auto" style="display:contents"><ol class="numbered-list" id="28dcbc69-4770-8065-88f9-dcd2a089d93c" start="5" type="1"><li>회사가 회원의 회원자격을 상실시키기로 결정한 경우에는 회원등록을 말소합니다.</li></ol></div><div dir="auto" style="display:contents"><ol class="numbered-list" id="28dcbc69-4770-80ea-9b7f-c26a9abcf8c4" start="6" type="1"><li>이용자가 본 약관에 의해서 회원 가입 후 서비스를 이용하는 도중, 연속하여 1년 동안 서비스를 이용하기 위해 로그인 기록이 없는 경우, 회사는 회원의 회원자격을 상실시킬 수 있습니다.</li></ol></div><div dir="auto" style="display:contents"><p class="" id="28dcbc69-4770-808f-a437-eca12907b7d7">
</p></div><div dir="auto" style="display:contents"><h3 class="" id="28dcbc69-4770-8095-be4c-f6fa018e3dd6"><strong>제7조 (회원에 대한 통지)</strong></h3></div><div dir="auto" style="display:contents"><ol class="numbered-list" id="28dcbc69-4770-80c8-b338-f06cf3288984" start="1" type="1"><li>회사가 불특정다수 회원에 대한 통지를 하는 경우 7일 이상 공지사항 게시판에 게시함으로써 개별 통지에 갈음할 수 있습니다.</li></ol></div><div dir="auto" style="display:contents"><p class="" id="28dcbc69-4770-80a8-b9a4-ccb75ba8053a">
</p></div><div dir="auto" style="display:contents"><h3 class="" id="28dcbc69-4770-8054-880f-e0a952de836b"><strong>제8조 (회원의 개인정보)</strong></h3></div><div dir="auto" style="display:contents"><p class="" id="28dcbc69-4770-80af-b145-d4923d4b2fba">회사는 서비스를 제공하기 위하여 관련 법령의 규정에 따라 회원으로부터 필요한 개인정보를 수집합니다. (개인정보에 대한 개별 항목은 <a href="https://www.notion.so/287cbc694770801687d8ec0ff7a68aea?pvs=21">개인정보처리방침</a>에서 고지)</p></div><div dir="auto" style="display:contents"><p class="" id="28dcbc69-4770-8032-bb87-d5c921e29c6a">
</p></div><div dir="auto" style="display:contents"><h3 class="" id="28dcbc69-4770-80fb-88ac-e98d68346e48"><strong>제9조 (회사의 의무)</strong></h3></div><div dir="auto" style="display:contents"><ol class="numbered-list" id="28dcbc69-4770-80f9-9d7c-fa7656356725" start="1" type="1"><li>회사는 법령과 본 약관이 금지하거나 공서양속에 반하는 행위를 하지 않으며 본 약관이 정하는 바에 따라 지속적이고, 안정적으로 서비스를 제공하기 위해서 노력합니다.</li></ol></div><div dir="auto" style="display:contents"><ol class="numbered-list" id="28dcbc69-4770-80ac-a21b-de2beb77a9ea" start="2" type="1"><li>회사는 회원이 안전하고 편리하게 서비스를 이용할 수 있도록 시스템을 구축합니다.</li></ol></div><div dir="auto" style="display:contents"><ol class="numbered-list" id="28dcbc69-4770-8044-af10-dc22321e8c85" start="3" type="1"><li>회사는 회원이 서비스를 이용함에 있어 회원에게 법률적인 증명이 가능한 고의 또는 중대한 과실을 입힐 경우 이로 인한 손해를 배상할 책임이 있습니다.</li></ol></div><div dir="auto" style="display:contents"><p class="" id="28dcbc69-4770-80c0-a777-f503d54b62a0">
</p></div><div dir="auto" style="display:contents"><h3 class="" id="28dcbc69-4770-8013-8409-ea79c102b138"><strong>제10조 (회원의 ID 및 비밀번호에 대한 의무)</strong></h3></div><div dir="auto" style="display:contents"><ol class="numbered-list" id="28dcbc69-4770-8031-8bb0-c563fd3a6596" start="1" type="1"><li>회사가 관계법령, "개인정보보호정책"에 의해서 그 책임을 지는 경우를 제외하고, 자신의 ID와 비밀번호에 관한 관리책임은 각 회원에게 있습니다.</li></ol></div><div dir="auto" style="display:contents"><ol class="numbered-list" id="28dcbc69-4770-80b6-9037-fa2522de12bd" start="2" type="1"><li>회원은 자신의 ID 및 비밀번호를 제3자에게 이용하게 해서는 안됩니다.</li></ol></div><div dir="auto" style="display:contents"><ol class="numbered-list" id="28dcbc69-4770-8073-9477-f6f494b44251" start="3" type="1"><li>회원은 자신의 ID 및 비밀번호를 도난당하거나 제3자가 사용하고 있음을 인지한 경우에는 바로 회사에 통보하고 회사의 안내가 있는 경우에는 그에 따라야 합니다.</li></ol></div><div dir="auto" style="display:contents"><p class="" id="28dcbc69-4770-80d9-b23d-dbfb76218825">
</p></div><div dir="auto" style="display:contents"><h3 class="" id="28dcbc69-4770-8014-8d6b-c61e54a45106"><strong>제11조 (회원의 의무)</strong></h3></div><div dir="auto" style="display:contents"><ol class="numbered-list" id="28dcbc69-4770-8066-bcdd-fc9c74004461" start="1" type="1"><li>회원은 다음 각 호의 행위를 하여서는 안됩니다.<div dir="auto" style="display:contents"><ul class="bulleted-list" id="28dcbc69-4770-801d-91e8-c238423369c9"><li style="list-style-type:disc">회원가입 신청 또는 변경 시 허위 내용을 등록하는 행위</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28dcbc69-4770-80c5-bba3-c7b1837e4ba1"><li style="list-style-type:disc">회사 및 제3자의 지적재산권을 침해하거나 회사의 권리와 업무 또는 제3자의 권리와 활동을 방해하는 행위</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28dcbc69-4770-8078-802a-fc6fce2a1c08"><li style="list-style-type:disc">다른 회원의 ID를 도용하는 행위</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28dcbc69-4770-80da-871b-dc1c1e0468d3"><li style="list-style-type:disc">관련 법령에 의하여 전송 또는 게시가 금지되는 정보(컴퓨터 프로그램 등)의 게시 또는 전송하는 행위</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28dcbc69-4770-807c-829a-fa3a0636f862"><li style="list-style-type:disc">회사의 직원 또는 서비스의 관리자를 가장하거나 타인의 명의를 도용하여 정보를 게시, 전송하는 행위</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28dcbc69-4770-80c6-b379-f48817fe7857"><li style="list-style-type:disc">컴퓨터 소프트웨어, 하드웨어, 전기통신 장비의 정상적인 가동을 방해, 파괴할 목적으로 고안된 소프트웨어 바이러스, 기타 컴퓨터 코드, 파일, 프로그램을 포함하고 있는 자료를 게시하거나 전송하는 행위</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28dcbc69-4770-80a1-aaf2-f4c85a022ed0"><li style="list-style-type:disc">스토킹(stalking) 등 다른 회원을 괴롭히는 행위</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28dcbc69-4770-80ca-9e64-edd10b62eeb2"><li style="list-style-type:disc">다른 회원에 대한 개인정보를 그 동의 없이 수집, 저장, 공개하는 행위</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28dcbc69-4770-805b-81fd-e0ec9c22b8b5"><li style="list-style-type:disc">불특정 다수의 자를 대상으로 하여 광고 또는 선전을 게시하거나 음란물을 게시하는 행위</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28dcbc69-4770-80ea-98cf-f3db571d74ba"><li style="list-style-type:disc">개인적인 감정이나 의견 차이만으로 특정 사용자의 활동을 방해하거나, 커뮤니티의 정상적인 운영을 방해할 목적으로 신고를 남발하는 행위</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28dcbc69-4770-8009-8dd3-f0cfc59a8dd5"><li style="list-style-type:disc">회사가 제공하는 서비스에 게시된 공지사항 규정을 위반하는 행위</li></ul></div></li></ol></div><div dir="auto" style="display:contents"><ol class="numbered-list" id="28dcbc69-4770-8075-be3c-faa07f4489e3" start="2" type="1"><li>제1항에 해당하는 행위를 한 회원이 있을 경우 회사는 본 약관 제6조 제2, 3항에서 정한 바에 따라 회원의 회원자격을 적절한 방법으로 제한 및 정지, 상실시킬 수 있습니다.</li></ol></div><div dir="auto" style="display:contents"><ol class="numbered-list" id="28dcbc69-4770-8034-a7fd-ff4f79bc0763" start="3" type="1"><li>회원은 그 귀책사유로 인하여 회사나 다른 회원이 입은 손해를 배상할 책임이 있습니다.</li></ol></div><div dir="auto" style="display:contents"><p class="" id="28dcbc69-4770-8039-ac4c-dcb5a7556c8e">
</p></div><div dir="auto" style="display:contents"><h3 class="" id="28dcbc69-4770-80f4-b5c6-c04025d85099"><strong>제12조 (공개 게시물의 삭제 또는 이용 제한)</strong></h3></div><div dir="auto" style="display:contents"><ol class="numbered-list" id="28dcbc69-4770-8067-b27b-cbd6c7f1ccf7" start="1" type="1"><li>회사의 소통 서비스(이하 “댓글”) 회원의 자유로운 소통과 지식정보 교환을 위해 마련되었습니다. 댓글을 이용하려면 댓글 운영 방침을 준수해야 하며 댓글 작성 시 이에 동의한 것으로 간주합니다. 운영 방침을 위반한 댓글 또는 회원이 신고 등에 의해 발견되면 담당자가 이를 검토할 수 있으며 해당 댓글을 숨김 및 삭제하거나 서비스 이용을 제한할 수 있습니다.<br/><br/><strong>제한되는 댓글 내용</strong><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28dcbc69-4770-803b-ae09-dbef9c62f655"><li style="list-style-type:disc">다른 회원 또는 제3자를 비방하거나 중상모략으로 명예를 손상시키는 내용</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28dcbc69-4770-8023-a4db-d5902d94abea"><li style="list-style-type:disc">음란물, 욕설 등 공서양속에 위반되는 내용의 정보, 문장, 도형 등을 유포하는 내용</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28dcbc69-4770-803f-adb6-de62c06d4a23"><li style="list-style-type:disc">성별이나 인종 및 특정 지역을 비하 또는 차별하는 게시물</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28dcbc69-4770-8059-b106-f1adb08fd3ff"><li style="list-style-type:disc">범죄 행위와 관련이 있다고 판단되는 내용</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28dcbc69-4770-807d-b606-d20b333b891d"><li style="list-style-type:disc">회사, 회원 또는 제3자의 저작권 등 기타 권리를 침해하는 내용</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28dcbc69-4770-8061-8780-e48345e0df1e"><li style="list-style-type:disc">종교적, 정치적 분쟁을 야기하는 내용으로서, 이러한 분쟁으로 인하여 회사의 업무가 방해되거나 방해되리라고 판단되는 경우</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28dcbc69-4770-8048-b5af-ffab02970d1d"><li style="list-style-type:disc">타인의 개인정보, 사생활을 침해하거나 명예를 손상시키는 경우</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28dcbc69-4770-80c3-9515-ef802ca027e0"><li style="list-style-type:disc">동일한 내용을 중복하여 다수 작성하는 등 소통 목적에 어긋나는 경우</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28dcbc69-4770-80e4-833f-c368c464e1bd"><li style="list-style-type:disc">불필요하거나 승인되지 않은 광고, 판촉물을 게재하는 경우</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28dcbc69-4770-80f6-ad5b-eca603d82c4f"><li style="list-style-type:disc">서비스 명칭 또는 회사 임직원 및 다른 회원을 사칭하는 경우</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28dcbc69-4770-80e5-ae65-e3b90acf9791"><li style="list-style-type:disc">커뮤니티 질서를 해치는 내용</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28dcbc69-4770-809d-afca-dac5190c1ed6"><li style="list-style-type:disc">기타 서비스 이용 약관에 어긋나는 내용</li></ul></div></li></ol></div><div dir="auto" style="display:contents"><ol class="numbered-list" id="28dcbc69-4770-80a4-b01b-d39f2a2e6966" start="2" type="1"><li>회원의 댓글로 인한 법률상 이익 침해를 근거로, 다른 회원 또는 제3자가 회원 또는 회사를 대상으로 하여 민형사상의 법적 조치(예:고소, 가처분신청, 손해배상청구소송)를 취하는 동시에 법적 조치와 관련된 댓글 삭제를 요청해오는 경우, 회사는 동 법적 조치의 결과(예: 검찰의 기소, 법원의 가처분결정, 손해배상판결)가 있을 때까지 관련 댓글에 대한 접근을 잠정적으로 제한할 수 있습니다.</li></ol></div><div dir="auto" style="display:contents"><p class="" id="28dcbc69-4770-80c1-ab04-db055d1242b2">
</p></div><div dir="auto" style="display:contents"><h3 class="" id="28dcbc69-4770-807f-aa08-c10e6fdd3863"><strong>제13조 (저작권의 귀속 및 댓글의 이용)</strong></h3></div><div dir="auto" style="display:contents"><ol class="numbered-list" id="28dcbc69-4770-8068-94cf-f23df5745a53" start="1" type="1"><li>회사가 작성한 저작물에 대한 저작권, 기타 지적재산권은 회사에 귀속합니다.</li></ol></div><div dir="auto" style="display:contents"><ol class="numbered-list" id="28dcbc69-4770-8069-99b6-d1d0fedd5dac" start="2" type="1"><li>회원은 회사가 제공하는 서비스를 이용함으로써 얻은 정보를 회사의 사전 승낙 없이 복제, 전송, 출판, 배포, 방송, 기타 방법에 의하여 영리목적으로 이용하거나 제3자에게 이용하게 하여서는 안됩니다.</li></ol></div><div dir="auto" style="display:contents"><ol class="numbered-list" id="28dcbc69-4770-8066-8db6-ee823e918b50" start="3" type="1"><li>회원이 서비스 내에 작성한 댓글의 저작권은 게시한 회원에게 귀속됩니다. 단, 회사는 서비스의 운영, 전시, 전송, 배포, 홍보의 목적으로 회원의 별도의 허락 없이 무상으로 저작권법에 규정하는 공정한 관행에 합치되게 합리적인 범위 내에서 다음과 같이 회원이 등록한 댓글을 사용할 수 있습니다.<div dir="auto" style="display:contents"><ul class="bulleted-list" id="28dcbc69-4770-80db-8232-f847ae28460a"><li style="list-style-type:disc">서비스 내에서 회원 댓글의 복제, 수정, 개조, 전시, 전송, 배포 및 저작물성을 해치지 않는 범위 내에서의 편집 저작물 작성</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28dcbc69-4770-800f-9a97-f2ba1c03c5f7"><li style="list-style-type:disc">미디어, 통신사 등 서비스 제휴 파트너에게 회원의 댓글 내용을 제공, 전시 혹은 홍보하게 하는 것. 단, 이 경우 회사는 별도의 동의 없이 회원의 이용자ID 외에 회원의 개인정보를 제공하지 않습니다.</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28dcbc69-4770-80f1-9775-f1897b37b7ec"><li style="list-style-type:disc">회사는 전항 이외의 방법으로 회원의 댓글을 이용하고자 하는 경우, 전화, 팩스, 전자우편 등의 방법을 통해 사전에 회원의 동의를 얻어야 합니다.</li></ul></div><div dir="auto" style="display:contents"><p class="" id="28dcbc69-4770-806c-8fe8-fe33ec7ddb81">
</p></div></li></ol></div><div dir="auto" style="display:contents"><h3 class="" id="28dcbc69-4770-80ef-bfcc-f4128622b0b5"><strong>제14조 (광고게재 및 광고주와의 거래)</strong></h3></div><div dir="auto" style="display:contents"><ol class="numbered-list" id="28dcbc69-4770-80f8-9da9-c1ce3a95b5a2" start="1" type="1"><li>회사가 회원에게 서비스를 제공할 수 있는 서비스 투자기반의 일부는 광고게재를 통한 수익으로부터 나옵니다. 회원은 회원이 등록한 댓글의 내용을 활용한 광고게재 및 기타 서비스 상에 노출되는 광고게재에 대해 동의합니다.</li></ol></div><div dir="auto" style="display:contents"><ol class="numbered-list" id="28dcbc69-4770-80ab-9ec0-cb34908bc7bc" start="2" type="1"><li>회사는 서비스 상에 게재되어 있거나 서비스를 통한 광고주의 판촉활동에 회원이 참여하거나 교신 또는 거래를 함으로써 발생하는 손실과 손해에 대해 책임을 지지 않습니다.</li></ol></div><div dir="auto" style="display:contents"><p class="" id="28dcbc69-4770-80f4-b157-db973200380a">
</p></div><div dir="auto" style="display:contents"><h3 class="" id="28dcbc69-4770-8002-ab04-e444f24a4793"><strong>제15조 (약관의 개정)</strong></h3></div><div dir="auto" style="display:contents"><ol class="numbered-list" id="28dcbc69-4770-80c0-812b-fb4419ddccc1" start="1" type="1"><li>회사는 약관의 규제 등에 관한 법률, 전자거래기본법, 전자서명법, 정보통신망 이용 촉진 등에 관한 법률 등 관련 법을 위배하지 않는 범위에서 본 약관을 개정할 수 있습니다.</li></ol></div><div dir="auto" style="display:contents"><ol class="numbered-list" id="28dcbc69-4770-8005-b843-cd40656a9105" start="2" type="1"><li>다만, 개정 내용이 회원에게 불리할 경우에는 적용일자 30일 이전부터 적용일자 전일까지 공지합니다.</li></ol></div><div dir="auto" style="display:contents"><ol class="numbered-list" id="28dcbc69-4770-808d-83b3-c67e7dddf3a2" start="3" type="1"><li>회원은 변경된 약관에 대해 거부할 권리가 있습니다. 회원은 변경된 약관이 공지된 후 15일 이내에 거부의사를 표명할 수 있습니다. 회원이 거부하는 경우 회사는 당해 회원과의 계약을 해지할 수 있습니다. 만약 회원이 변경된 약관이 공지된 후 15일 이내에 거부의사를 표시하지 않는 경우에는 동의하는 것으로 간주합니다.</li></ol></div><div dir="auto" style="display:contents"><p class="" id="28dcbc69-4770-809e-b078-f27b60f6df85">
</p></div><div dir="auto" style="display:contents"><h3 class="" id="28dcbc69-4770-8091-aa96-ed3aa3e67e2b"><strong>제16조 (재판관할)</strong></h3></div><div dir="auto" style="display:contents"><p class="" id="28dcbc69-4770-8097-9d39-c5894b259055">회사와 회원 간에 발생한 서비스 이용에 관한 분쟁에 대하여는 대한민국 법을 적용하며, 본 분쟁으로 인한 소는 민사소송법상의 관할을 가지는 대한민국의 법원에 제기합니다.</p></div><div dir="auto" style="display:contents"><p class="" id="28dcbc69-4770-80c5-b934-e1c92ca2eff1">
</p></div><div dir="auto" style="display:contents"><p class="" id="28dcbc69-4770-8028-8824-c40c4f03a742">
</p></div><div dir="auto" style="display:contents"><p class="" id="28dcbc69-4770-8051-9f95-ecd4023cba86">공고일자 : 2024. 08. 20.</p></div><div dir="auto" style="display:contents"><p class="" id="28dcbc69-4770-8058-beed-d0cc7b3b0c87">시행일자 : 2024. 09. 12.</p></div><div dir="auto" style="display:contents"><p class="" id="301cbc69-4770-8093-be19-f1c69a1cbc0b">
</p></div></div>
</article>
  $terms_of_service$,
  1,
  TRUE,
  NULL
)
ON CONFLICT (name, version) DO NOTHING;

-- 개인정보 처리방침 (version=1, required=true)
INSERT INTO terms (name, title, content, version, required, effective_from)
VALUES (
  'PRIVACY_POLICY',
  '개인정보 처리방침',
  $privacy_policy$
<style>
/* cspell:disable-file */
/* webkit printing magic: print all background colors */
html {
	-webkit-print-color-adjust: exact;
}
* {
	box-sizing: border-box;
	-webkit-print-color-adjust: exact;
}

html,
body {
	margin: 0;
	padding: 0;
}
@media only screen {
	body {
		margin: 2em auto;
		max-width: 900px;
		color: rgb(55, 53, 47);
	}
}

body {
	line-height: 1.5;
	white-space: pre-wrap;
}

a,
a.visited {
	color: inherit;
	text-decoration: underline;
}

.pdf-relative-link-path {
	font-size: 80%;
	color: #444;
}

h1,
h2,
h3 {
	letter-spacing: -0.01em;
	line-height: 1.2;
	font-weight: 600;
	margin-bottom: 0;
}

/* Override strong tags inside headings to maintain consistent weight */
h1 strong,
h2 strong,
h3 strong {
	font-weight: 600;
}

.page-title {
	font-size: 2.5rem;
	font-weight: 700;
	margin-top: 0;
	margin-bottom: 0.75em;
}

h1 {
	font-size: 1.875rem;
	margin-top: 1.875rem;
}

h2 {
	font-size: 1.5rem;
	margin-top: 1.5rem;
}

h3 {
	font-size: 1.25rem;
	margin-top: 1.25rem;
}

.source {
	border: 1px solid #ddd;
	border-radius: 3px;
	padding: 1.5em;
	word-break: break-all;
}

.callout {
	border-radius: 10px;
	padding: 1rem;
}

figure {
	margin: 1.25em 0;
	page-break-inside: avoid;
}

figcaption {
	opacity: 0.5;
	font-size: 85%;
	margin-top: 0.5em;
}

mark {
	background-color: transparent;
}

.indented {
	padding-left: 1.5em;
}

hr {
	background: transparent;
	display: block;
	width: 100%;
	height: 1px;
	visibility: visible;
	border: none;
	border-bottom: 1px solid rgba(55, 53, 47, 0.09);
}

img {
	max-width: 100%;
}

@media only print {
	img {
		max-height: 100vh;
		object-fit: contain;
	}
}

@page {
	margin: 1in;
}

.collection-content {
	font-size: 0.875rem;
}

.collection-content td {
	white-space: pre-wrap;
	word-break: break-word;
}

.column-list {
	display: flex;
	justify-content: space-between;
}

.column {
	padding: 0 1em;
}

.column:first-child {
	padding-left: 0;
}

.column:last-child {
	padding-right: 0;
}

.table_of_contents-item {
	display: block;
	font-size: 0.875rem;
	line-height: 1.3;
	padding: 0.125rem;
}

.table_of_contents-indent-1 {
	margin-left: 1.5rem;
}

.table_of_contents-indent-2 {
	margin-left: 3rem;
}

.table_of_contents-indent-3 {
	margin-left: 4.5rem;
}

.table_of_contents-link {
	text-decoration: none;
	opacity: 0.7;
	border-bottom: 1px solid rgba(55, 53, 47, 0.18);
}

table,
th,
td {
	border: 1px solid rgba(55, 53, 47, 0.09);
	border-collapse: collapse;
}

table {
	border-left: none;
	border-right: none;
}

th,
td {
	font-weight: normal;
	padding: 0.25em 0.5em;
	line-height: 1.5;
	min-height: 1.5em;
	text-align: left;
}

th {
	color: rgba(55, 53, 47, 0.6);
}

ol,
ul {
	margin: 0;
	margin-block-start: 0.6em;
	margin-block-end: 0.6em;
}

li > ol:first-child,
li > ul:first-child {
	margin-block-start: 0.6em;
}

ul > li {
	list-style: disc;
}

ul.to-do-list {
	padding-inline-start: 0;
}

ul.to-do-list > li {
	list-style: none;
}

.to-do-children-checked {
	text-decoration: line-through;
	opacity: 0.375;
}

ul.toggle > li {
	list-style: none;
}

ul {
	padding-inline-start: 1.7em;
}

ul > li {
	padding-left: 0.1em;
}

ol {
	padding-inline-start: 1.6em;
}

ol > li {
	padding-left: 0.2em;
}

.mono ol {
	padding-inline-start: 2em;
}

.mono ol > li {
	text-indent: -0.4em;
}

.toggle {
	padding-inline-start: 0em;
	list-style-type: none;
}

/* Indent toggle children */
.toggle > li > details {
	padding-left: 1.7em;
}

.toggle > li > details > summary {
	margin-left: -1.1em;
}

.selected-value {
	display: inline-block;
	padding: 0 0.5em;
	background: rgba(206, 205, 202, 0.5);
	border-radius: 3px;
	margin-right: 0.5em;
	margin-top: 0.3em;
	margin-bottom: 0.3em;
	white-space: nowrap;
}

.collection-title {
	display: inline-block;
	margin-right: 1em;
}

.page-description {
	margin-bottom: 2em;
}

.simple-table {
	margin-top: 1em;
	font-size: 0.875rem;
	empty-cells: show;
}
.simple-table td {
	height: 29px;
	min-width: 120px;
}

.simple-table th {
	height: 29px;
	min-width: 120px;
}

.simple-table-header-color {
	background: rgb(247, 246, 243);
	color: black;
}
.simple-table-header {
	font-weight: 500;
}

time {
	opacity: 0.5;
}

.icon {
	display: inline-flex;
	align-items: center;
	justify-content: center;
	max-width: 1.2em;
	max-height: 1.2em;
	text-decoration: none;
	vertical-align: text-bottom;
	margin-right: 0.5em;
}

img.icon {
	border-radius: 3px;
}

.callout img.notion-static-icon {
	width: 1em;
	height: 1em;
}

.callout p {
	margin: 0;
}

.callout h1,
.callout h2,
.callout h3 {
	margin: 0 0 0.6rem;
}

.user-icon {
	width: 1.5em;
	height: 1.5em;
	border-radius: 100%;
	margin-right: 0.5rem;
}

.user-icon-inner {
	font-size: 0.8em;
}

.text-icon {
	border: 1px solid #000;
	text-align: center;
}

.page-cover-image {
	display: block;
	object-fit: cover;
	width: 100%;
	max-height: 30vh;
}

.page-header-icon {
	font-size: 3rem;
	margin-bottom: 1rem;
}

.page-header-icon-with-cover {
	margin-top: -0.72em;
	margin-left: 0.07em;
}

.page-header-icon img {
	border-radius: 3px;
}

.link-to-page {
	margin: 1em 0;
	padding: 0;
	border: none;
	font-weight: 500;
}

p > .user {
	opacity: 0.5;
}

td > .user,
td > time {
	white-space: nowrap;
}

input[type="checkbox"] {
	transform: scale(1.5);
	margin-right: 0.6em;
	vertical-align: middle;
}

p {
	margin-top: 0.5em;
	margin-bottom: 0.5em;
}

.image {
	border: none;
	margin: 1.5em 0;
	padding: 0;
	border-radius: 0;
	text-align: center;
}

.code,
code {
	background: rgba(135, 131, 120, 0.15);
	border-radius: 3px;
	padding: 0.2em 0.4em;
	border-radius: 3px;
	font-size: 85%;
	tab-size: 2;
}

code {
	color: #eb5757;
}

.code {
	padding: 1.5em 1em;
}

.code-wrap {
	white-space: pre-wrap;
	word-break: break-all;
}

.code > code {
	background: none;
	padding: 0;
	font-size: 100%;
	color: inherit;
}

blockquote {
	font-size: 1em;
	margin: 1em 0;
	padding-left: 1em;
	border-left: 3px solid rgb(55, 53, 47);
}

blockquote.quote-large {
	font-size: 1.25em;
}

.bookmark {
	text-decoration: none;
	max-height: 8em;
	padding: 0;
	display: flex;
	width: 100%;
	align-items: stretch;
}

.bookmark-title {
	font-size: 0.85em;
	overflow: hidden;
	text-overflow: ellipsis;
	height: 1.75em;
	white-space: nowrap;
}

.bookmark-text {
	display: flex;
	flex-direction: column;
}

.bookmark-info {
	flex: 4 1 180px;
	padding: 12px 14px 14px;
	display: flex;
	flex-direction: column;
	justify-content: space-between;
}

.bookmark-image {
	width: 33%;
	flex: 1 1 180px;
	display: block;
	position: relative;
	object-fit: cover;
	border-radius: 1px;
}

.bookmark-description {
	color: rgba(55, 53, 47, 0.6);
	font-size: 0.75em;
	overflow: hidden;
	max-height: 4.5em;
	word-break: break-word;
}

.bookmark-href {
	font-size: 0.75em;
	margin-top: 0.25em;
}

.sans { font-family: ui-sans-serif, -apple-system, BlinkMacSystemFont, "Segoe UI Variable Display", "Segoe UI", Helvetica, "Apple Color Emoji", "Noto Sans Arabic", "Noto Sans Hebrew", Arial, sans-serif, "Segoe UI Emoji", "Segoe UI Symbol"; }
.code { font-family: "SFMono-Regular", Menlo, Consolas, "PT Mono", "Liberation Mono", Courier, monospace; }
.serif { font-family: Lyon-Text, Georgia, ui-serif, serif; }
.mono { font-family: iawriter-mono, Nitti, Menlo, Courier, monospace; }
.pdf .sans { font-family: Inter, ui-sans-serif, -apple-system, BlinkMacSystemFont, "Segoe UI Variable Display", "Segoe UI", Helvetica, "Apple Color Emoji", "Noto Sans Arabic", "Noto Sans Hebrew", Arial, sans-serif, "Segoe UI Emoji", "Segoe UI Symbol", 'Twemoji', 'Noto Color Emoji', 'Noto Sans CJK JP'; }
.pdf:lang(zh-CN) .sans { font-family: Inter, ui-sans-serif, -apple-system, BlinkMacSystemFont, "Segoe UI Variable Display", "Segoe UI", Helvetica, "Apple Color Emoji", "Noto Sans Arabic", "Noto Sans Hebrew", Arial, sans-serif, "Segoe UI Emoji", "Segoe UI Symbol", 'Twemoji', 'Noto Color Emoji', 'Noto Sans CJK SC'; }
.pdf:lang(zh-TW) .sans { font-family: Inter, ui-sans-serif, -apple-system, BlinkMacSystemFont, "Segoe UI Variable Display", "Segoe UI", Helvetica, "Apple Color Emoji", "Noto Sans Arabic", "Noto Sans Hebrew", Arial, sans-serif, "Segoe UI Emoji", "Segoe UI Symbol", 'Twemoji', 'Noto Color Emoji', 'Noto Sans CJK TC'; }
.pdf:lang(ko-KR) .sans { font-family: Inter, ui-sans-serif, -apple-system, BlinkMacSystemFont, "Segoe UI Variable Display", "Segoe UI", Helvetica, "Apple Color Emoji", "Noto Sans Arabic", "Noto Sans Hebrew", Arial, sans-serif, "Segoe UI Emoji", "Segoe UI Symbol", 'Twemoji', 'Noto Color Emoji', 'Noto Sans CJK KR'; }
.pdf .code { font-family: Source Code Pro, "SFMono-Regular", Menlo, Consolas, "PT Mono", "Liberation Mono", Courier, monospace, 'Twemoji', 'Noto Color Emoji', 'Noto Sans Mono CJK JP'; }
.pdf:lang(zh-CN) .code { font-family: Source Code Pro, "SFMono-Regular", Menlo, Consolas, "PT Mono", "Liberation Mono", Courier, monospace, 'Twemoji', 'Noto Color Emoji', 'Noto Sans Mono CJK SC'; }
.pdf:lang(zh-TW) .code { font-family: Source Code Pro, "SFMono-Regular", Menlo, Consolas, "PT Mono", "Liberation Mono", Courier, monospace, 'Twemoji', 'Noto Color Emoji', 'Noto Sans Mono CJK TC'; }
.pdf:lang(ko-KR) .code { font-family: Source Code Pro, "SFMono-Regular", Menlo, Consolas, "PT Mono", "Liberation Mono", Courier, monospace, 'Twemoji', 'Noto Color Emoji', 'Noto Sans Mono CJK KR'; }
.pdf .serif { font-family: PT Serif, Lyon-Text, Georgia, ui-serif, serif, 'Twemoji', 'Noto Color Emoji', 'Noto Serif CJK JP'; }
.pdf:lang(zh-CN) .serif { font-family: PT Serif, Lyon-Text, Georgia, ui-serif, serif, 'Twemoji', 'Noto Color Emoji', 'Noto Serif CJK SC'; }
.pdf:lang(zh-TW) .serif { font-family: PT Serif, Lyon-Text, Georgia, ui-serif, serif, 'Twemoji', 'Noto Color Emoji', 'Noto Serif CJK TC'; }
.pdf:lang(ko-KR) .serif { font-family: PT Serif, Lyon-Text, Georgia, ui-serif, serif, 'Twemoji', 'Noto Color Emoji', 'Noto Serif CJK KR'; }
.pdf .mono { font-family: PT Mono, iawriter-mono, Nitti, Menlo, Courier, monospace, 'Twemoji', 'Noto Color Emoji', 'Noto Sans Mono CJK JP'; }
.pdf:lang(zh-CN) .mono { font-family: PT Mono, iawriter-mono, Nitti, Menlo, Courier, monospace, 'Twemoji', 'Noto Color Emoji', 'Noto Sans Mono CJK SC'; }
.pdf:lang(zh-TW) .mono { font-family: PT Mono, iawriter-mono, Nitti, Menlo, Courier, monospace, 'Twemoji', 'Noto Color Emoji', 'Noto Sans Mono CJK TC'; }
.pdf:lang(ko-KR) .mono { font-family: PT Mono, iawriter-mono, Nitti, Menlo, Courier, monospace, 'Twemoji', 'Noto Color Emoji', 'Noto Sans Mono CJK KR'; }
.highlight-default {
	color: rgba(44, 44, 43, 1);
}
.highlight-gray {
	color: rgba(125, 122, 117, 1);
	fill: rgba(125, 122, 117, 1);
}
.highlight-brown {
	color: rgba(159, 118, 90, 1);
	fill: rgba(159, 118, 90, 1);
}
.highlight-orange {
	color: rgba(210, 123, 45, 1);
	fill: rgba(210, 123, 45, 1);
}
.highlight-yellow {
	color: rgba(203, 148, 52, 1);
	fill: rgba(203, 148, 52, 1);
}
.highlight-teal {
	color: rgba(80, 148, 110, 1);
	fill: rgba(80, 148, 110, 1);
}
.highlight-blue {
	color: rgba(56, 125, 201, 1);
	fill: rgba(56, 125, 201, 1);
}
.highlight-purple {
	color: rgba(154, 107, 180, 1);
	fill: rgba(154, 107, 180, 1);
}
.highlight-pink {
	color: rgba(193, 76, 138, 1);
	fill: rgba(193, 76, 138, 1);
}
.highlight-red {
	color: rgba(207, 81, 72, 1);
	fill: rgba(207, 81, 72, 1);
}
.highlight-default_background {
	color: rgba(44, 44, 43, 1);
}
.highlight-gray_background {
	background: rgba(42, 28, 0, 0.07);
}
.highlight-brown_background {
	background: rgba(139, 46, 0, 0.086);
}
.highlight-orange_background {
	background: rgba(224, 101, 1, 0.129);
}
.highlight-yellow_background {
	background: rgba(211, 168, 0, 0.137);
}
.highlight-teal_background {
	background: rgba(0, 100, 45, 0.09);
}
.highlight-blue_background {
	background: rgba(0, 124, 215, 0.094);
}
.highlight-purple_background {
	background: rgba(102, 0, 178, 0.078);
}
.highlight-pink_background {
	background: rgba(197, 0, 93, 0.086);
}
.highlight-red_background {
	background: rgba(223, 22, 0, 0.094);
}
.block-color-default {
	color: inherit;
	fill: inherit;
}
.block-color-gray {
	color: rgba(125, 122, 117, 1);
	fill: rgba(125, 122, 117, 1);
}
.block-color-brown {
	color: rgba(159, 118, 90, 1);
	fill: rgba(159, 118, 90, 1);
}
.block-color-orange {
	color: rgba(210, 123, 45, 1);
	fill: rgba(210, 123, 45, 1);
}
.block-color-yellow {
	color: rgba(203, 148, 52, 1);
	fill: rgba(203, 148, 52, 1);
}
.block-color-teal {
	color: rgba(80, 148, 110, 1);
	fill: rgba(80, 148, 110, 1);
}
.block-color-blue {
	color: rgba(56, 125, 201, 1);
	fill: rgba(56, 125, 201, 1);
}
.block-color-purple {
	color: rgba(154, 107, 180, 1);
	fill: rgba(154, 107, 180, 1);
}
.block-color-pink {
	color: rgba(193, 76, 138, 1);
	fill: rgba(193, 76, 138, 1);
}
.block-color-red {
	color: rgba(207, 81, 72, 1);
	fill: rgba(207, 81, 72, 1);
}
.block-color-default_background {
	color: inherit;
	fill: inherit;
}
.block-color-gray_background {
	background: rgba(240, 239, 237, 1);
}
.block-color-brown_background {
	background: rgba(245, 237, 233, 1);
}
.block-color-orange_background {
	background: rgba(251, 235, 222, 1);
}
.block-color-yellow_background {
	background: rgba(249, 243, 220, 1);
}
.block-color-teal_background {
	background: rgba(232, 241, 236, 1);
}
.block-color-blue_background {
	background: rgba(229, 242, 252, 1);
}
.block-color-purple_background {
	background: rgba(243, 235, 249, 1);
}
.block-color-pink_background {
	background: rgba(250, 233, 241, 1);
}
.block-color-red_background {
	background: rgba(252, 233, 231, 1);
}
.select-value-color-default { background-color: rgba(42, 28, 0, 0.07); }
.select-value-color-gray { background-color: rgba(28, 19, 1, 0.11); }
.select-value-color-brown { background-color: rgba(127, 51, 0, 0.156); }
.select-value-color-orange { background-color: rgba(196, 88, 0, 0.203); }
.select-value-color-yellow { background-color: rgba(209, 156, 0, 0.282); }
.select-value-color-green { background-color: rgba(0, 96, 38, 0.156); }
.select-value-color-blue { background-color: rgba(0, 118, 217, 0.203); }
.select-value-color-purple { background-color: rgba(92, 0, 163, 0.141); }
.select-value-color-pink { background-color: rgba(183, 0, 78, 0.152); }
.select-value-color-red { background-color: rgba(206, 24, 0, 0.164); }

.checkbox {
	display: inline-flex;
	vertical-align: text-bottom;
	width: 16;
	height: 16;
	background-size: 16px;
	margin-left: 2px;
	margin-right: 5px;
}

.checkbox-on {
	background-image: url("data:image/svg+xml;charset=UTF-8,%3Csvg%20width%3D%2216%22%20height%3D%2216%22%20viewBox%3D%220%200%2016%2016%22%20fill%3D%22none%22%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%3E%0A%3Crect%20width%3D%2216%22%20height%3D%2216%22%20fill%3D%22%2358A9D7%22%2F%3E%0A%3Cpath%20d%3D%22M6.71429%2012.2852L14%204.9995L12.7143%203.71436L6.71429%209.71378L3.28571%206.2831L2%207.57092L6.71429%2012.2852Z%22%20fill%3D%22white%22%2F%3E%0A%3C%2Fsvg%3E");
}

.checkbox-off {
	background-image: url("data:image/svg+xml;charset=UTF-8,%3Csvg%20width%3D%2216%22%20height%3D%2216%22%20viewBox%3D%220%200%2016%2016%22%20fill%3D%22none%22%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%3E%0A%3Crect%20x%3D%220.75%22%20y%3D%220.75%22%20width%3D%2214.5%22%20height%3D%2214.5%22%20fill%3D%22white%22%20stroke%3D%22%2336352F%22%20stroke-width%3D%221.5%22%2F%3E%0A%3C%2Fsvg%3E");
}
	
</style>
<article class="page sans">
<div class="page-body"><div dir="auto" style="display:contents"><h2 class="" id="287cbc69-4770-80c0-b7d8-d054c9c6cd82">개인정보 처리방침</h2></div><div dir="auto" style="display:contents"><p class="" id="287cbc69-4770-8030-9c96-fbb7a03edd69">본 개인정보 처리방침은 2025년 09월 01일부터 시행됩니다.</p></div><div dir="auto" style="display:contents"><p class="" id="287cbc69-4770-80bf-9048-dce6a44557dc">
</p></div><div dir="auto" style="display:contents"><p class="" id="287cbc69-4770-802a-b0ee-fc447b33579f">마이폴리(이하 ‘회사’라 함)가 취급하는 모든 개인정보는 관련법령에 근거하거나 정보주체로부터 동의를 받고 수집‧보유 및 처리되고 있습니다.</p></div><div dir="auto" style="display:contents"><p class="" id="287cbc69-4770-80fd-805e-d4fc5f91bb80">회사는 법령의 규정에 따라 수집․보유 및 처리하는 개인정보를 공공업무의 적절한 수행과 정보주체의 자유와 권리 보호를 위해 「개인정보 보호법」 및 관계 법령이 정한 바를 준수하여, 적법하게 개인정보를 처리하고 안전하게 관리하고 있습니다. 이에 「개인정보 보호법」 제30조에 따라 정보주체에게 개인정보 처리에 관한 절차 및 기준을 안내하고, 이와 관련한 고충을 신속하고 원활하게 처리할 수 있도록 하기 위하여 다음과 같이 개인정보 처리방침을 수립·공개합니다.</p></div><div dir="auto" style="display:contents"><p class="" id="287cbc69-4770-809a-8cb3-ea5226a4a1ff">
</p></div><div dir="auto" style="display:contents"><h3 class="" id="287cbc69-4770-80c2-be57-fd5d8b384f58">목차</h3></div><div dir="auto" style="display:contents"><ol class="numbered-list" id="e994e36a-9a29-4713-b339-b4590af2269e" start="1" type="1"><li>개인정보 처리 목적</li></ol></div><div dir="auto" style="display:contents"><ol class="numbered-list" id="287cbc69-4770-805e-b943-de464a25f181" start="2" type="1"><li>개인정보 보유 기간 및 파기</li></ol></div><div dir="auto" style="display:contents"><ol class="numbered-list" id="287cbc69-4770-803a-a90b-e36ad001b94e" start="3" type="1"><li>이용자 및 법정대리인의 권리 및 행사 방법</li></ol></div><div dir="auto" style="display:contents"><ol class="numbered-list" id="287cbc69-4770-80bd-b6fd-e3bb6225f54f" start="4" type="1"><li>개인정보 안전성 확보 조치에 관한 사항</li></ol></div><div dir="auto" style="display:contents"><ol class="numbered-list" id="287cbc69-4770-805d-9de8-ce882a48eaea" start="5" type="1"><li>개인정보보호 책임자 및 고충 처리 부서</li></ol></div><div dir="auto" style="display:contents"><ol class="numbered-list" id="287cbc69-4770-8095-be4d-ff18782fb1b2" start="6" type="1"><li>개정 전 고지 의무 등 안내</li></ol></div><div dir="auto" style="display:contents"><p class="" id="287cbc69-4770-8067-b5a5-e3ade7559ee3">
</p></div><div dir="auto" style="display:contents"><h3 class="" id="287cbc69-4770-802a-a8f0-d43de6eb1108">1. 개인정보 처리 목적</h3></div><div dir="auto" style="display:contents"><p class="" id="e0182dee-9278-4356-b6dd-9c40fe312b0d">앱에서 회원가입 시 또는 서비스 이용에 필요한 최소한의 개인정보를 수집하고 있으며, 서비스 제공을 위해 반드시 필요한 최소한의 정보가 필수 항목으로 구성되어 있습니다. 그 외 추가 수집하는 정보는 선택 항목으로 동의를 받고 있으며, 선택 항목에 동의하지 않은 경우에도 서비스 이용에 제한은 없습니다.</p></div><div dir="auto" style="display:contents"><p class="" id="287cbc69-4770-8014-87a6-e8f480fafee4">
</p></div><div dir="auto" style="display:contents"><p class="" id="287cbc69-4770-80d5-be61-d3beefae022f"><strong>[개인정보 수집 항목]</strong></p></div><div dir="auto" style="display:contents"><p class="" id="287cbc69-4770-804d-a688-c6cbb1d37838">1) 회원가입 시<div class="indented"><div dir="auto" style="display:contents"><p class="" id="bdd4487f-3b09-4ec7-a792-7e0c6d8edd5c">필수</p></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="287cbc69-4770-80ac-b819-d46e94de48bd"><li style="list-style-type:disc">아이디, 이메일, 비밀번호, 이름(닉네임), 프로필 사진, 친구 목록, 카카오톡 전화번호(카카오톡 이용자의 경우에 한함), 연락처, 서비스 이용 내역, 서비스 내 구매 및 결제 내역</li></ul></div><div dir="auto" style="display:contents"><p class="" id="02ed5852-4cda-4c61-81a8-d5244414e23c">선택</p></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="287cbc69-4770-800d-94fc-e7439b271761"><li style="list-style-type:disc">생년월일, 성별</li></ul></div></div></p></div><div dir="auto" style="display:contents"><p class="" id="287cbc69-4770-80a2-a526-fdf775a33499">2) 법정대리인 동의 시<div class="indented"><div dir="auto" style="display:contents"><ul class="bulleted-list" id="287cbc69-4770-8081-9948-ff6c45aa533e"><li style="list-style-type:disc">법정대리인 정보(이름, 성별, 생년월일, 휴대전화번호, 통신사업자, 내/외국인 여부, 암호화된 이용자 확인값(CI), 중복가입확인정보(DI))</li></ul></div></div></p></div><div dir="auto" style="display:contents"><p class="" id="287cbc69-4770-803d-9076-ea6af7a94172">3) 서비스 이용 시<div class="indented"><div dir="auto" style="display:contents"><ul class="bulleted-list" id="287cbc69-4770-8054-b00f-e5923c02c10a"><li style="list-style-type:disc"><mark class="highlight-red">서비스에 따라 작성 필요</mark></li></ul></div></div></p></div><div dir="auto" style="display:contents"><p class="" id="287cbc69-4770-8039-957c-f563945c8900">4) 고객 상담 및 고충 처리 시</p></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="287cbc69-4770-8031-9744-ebdda1dee292"><li style="list-style-type:disc">아이디, 이메일, 이름, 연락처 </li></ul></div><div dir="auto" style="display:contents"><p class="" id="287cbc69-4770-8037-92bd-d4a9f76951dc">
</p></div><div dir="auto" style="display:contents"><p class="" id="287cbc69-4770-80f7-82a7-c398254cc0c5"><strong>[개인정보 수집 방법]</strong></p></div><div dir="auto" style="display:contents"><p class="" id="c187e6dc-07c2-4a7b-95f2-bc9a4c94ed2e">개인정보를 수집하는 경우에는 원칙적으로 사전에 이용자에게 해당 사실을 알리고 동의를 구하고 있으며, 아래와 같은 방법을 통해 개인정보를 수집합니다.</p></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="4978f9e1-79ac-4c44-bce5-087280132d1d"><li style="list-style-type:disc">회원가입 및 서비스 이용 과정에서 이용자가 개인정보 수집에 대해 동의를 하고 직접 정보를 입력하는 경우</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="c1a46375-a15c-4ba9-920a-df5689567361"><li style="list-style-type:disc">제휴 서비스 또는 단체 등으로부터 개인정보를 제공받은 경우</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="7c5e667d-c986-4cf2-82bf-6025b79ee7b3"><li style="list-style-type:disc">고객센터를 통한 상담 과정에서 웹페이지, 메일, 팩스, 전화 등</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="923cd2d3-07d0-4f23-af7e-3d5bf540bacb"><li style="list-style-type:disc">온·오프라인에서 진행되는 이벤트/행사 등 참여</li></ul></div><div dir="auto" style="display:contents"><p class="" id="287cbc69-4770-808f-a44c-c7a9ce18cd56">
</p></div><div dir="auto" style="display:contents"><p class="" id="287cbc69-4770-8053-b1f0-f313a582c158"><strong>[서비스 이용 과정에서 자동으로 생성되는 개인정보에 대한 수집 안내]</strong></p></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="77508a10-d155-4e94-b0f1-4ec89f5ba259"><li style="list-style-type:disc">서비스 이용 내역 : 서비스 이용 과정에서 자동화된 방법으로 생성되거나 이용자가 입력한 정보가 송수신되면서 회사 서버에 자동으로 기록 및 수집될 수 있는 정보를 의미합니다. 이와 같은 정보는 다른 정보와의 결합 여부, 처리하는 방식 등에 따라 개인정보에 해당할 수 있고 개인정보에 해당하지 않을 수도 있습니다.</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="287cbc69-4770-80a0-a8b1-e43a3e9bdc8f"><li style="list-style-type:disc">서비스 이용 내역에는 이용자가 입력 및 공유한 콘텐츠, 이용자가 입력한 검색어, 방문 및 접속 기록, 서비스 부정 이용 기록 등이 포함될 수 있습니다. 회사는 서비스 이용 기록 등의 정보를 서비스 제공 목적으로 처리할 수 있으며 필요한 경우에는 이용자의 추가 동의 등을 받고 이용할 수 있습니다.</li></ul></div><div dir="auto" style="display:contents"><p class="" id="287cbc69-4770-80c3-a130-e746514a3fe3">
</p></div><div dir="auto" style="display:contents"><p class="" id="287cbc69-4770-8075-b160-eb9bc7374b09"><strong>[개인정보 이용]</strong></p></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="35a09cda-d2ba-405c-96e1-0aceaaf8c606"><li style="list-style-type:disc">회원 식별/가입의사 확인, 본인/연령 확인</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="1b29e89b-d390-4e8d-ac9a-7dcae236c21c"><li style="list-style-type:disc">문의사항 또는 불만처리, 공지사항 전달</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="97f096a3-1b3e-49ea-a723-12cb555ba064"><li style="list-style-type:disc">서비스의 원활한 운영에 지장을 주는 행위(계정 도용 및 부정 이용 행위 등 포함)에 대한 방지 및 제재</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="5c08be7d-4435-4671-91ab-9afdc6612da9"><li style="list-style-type:disc">인구통계학적 특성과 이용자의 관심, 기호, 성향의 추정을 통한 맞춤형 콘텐츠 추천 및 이벤트, 광고 등 마케팅에 활용</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="c74816e6-cc39-43ab-8c33-fcb4c7353502"><li style="list-style-type:disc">신규 서비스 개발 및 서비스 기능 개선, 개인화된 서비스 제공, 프라이버시 보호를 위한 서비스 환경 구축</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="95339d29-d82b-4869-a2ec-ff73bef545d1"><li style="list-style-type:disc">서비스 이용 기록, 접속 빈도 및 서비스 이용에 대한 통계</li></ul></div><div dir="auto" style="display:contents"><p class="" id="287cbc69-4770-805e-98ec-d2e8e5b4a7c4">
</p></div><div dir="auto" style="display:contents"><p class="" id="287cbc69-4770-8090-86f8-de8c942e639a"><strong>[추가적인 개인정보 이용•제공 안내]</strong></p></div><div dir="auto" style="display:contents"><p class="" id="9d63f2df-2910-470f-9799-6b901fec1286">수집 목적과 합리적으로 관련된 범위에서는 법령에 따라 이용자의 동의 없이 개인정보를 이용하거나 제 3자에게 제공할 수 있습니다. 이때 ‘당초 수집 목적과 관련성이 있는지, 수집한 정황 또는 처리 관행에 비추어 볼 때 개인정보의 추가적인 이용 또는 제공에 대한 예측 가능성이 있는지, 이용자의 이익을 부당하게 침해하는지, 가명처리 또는 암호화 등 안전성 확보에 필요한 조치를 하였는지’를 종합적으로 고려합니다.</p></div><div dir="auto" style="display:contents"><p class="" id="5116670e-d2f7-4b35-9d12-ae698ebff79d">회사는 수집한 개인정보를 특정 개인을 알아볼 수 없도록 가명 처리하여 통계 작성, 과학적 연구, 공익적 기록 보존 등을 위하여 처리할 수 있습니다. 이 때 가명 정보는 재식별되지 않도록 추가정보와 분리하여 별도 저장・관리하고 필요한 기술적・관리적 보호조치를 취합니다.</p></div><div dir="auto" style="display:contents"><p class="" id="7d876c5b-6fe6-4f7c-b7a9-c19fd4c071ff">먼저, 가명 정보에 접근할 수 있는 권한은 최소한의 인원으로 제한하며, 접근 권한을 관리하고 있습니다. 가명 정보를 보호하기 위해 보안 시스템을 운영하며, 정기적인 내부 감사를 통해 가명 처리 및 보호 조치가 적절하게 이행되고 있는지 확인하고 개선 사항을 지속적으로 반영합니다. 또한, 가명 정보를 취급하는 직원들에게 정기적으로 교육을 실시하고 있습니다.</p></div><div dir="auto" style="display:contents"><p class="" id="287cbc69-4770-80c1-b373-e78243e54fcd">
</p></div><div dir="auto" style="display:contents"><h3 class="" id="287cbc69-4770-80f4-9213-ccf5d88469db">2. 개인정보 보유 기간 및 파기</h3></div><div dir="auto" style="display:contents"><p class="" id="28ccbc69-4770-8060-bbee-fd0177fb2e71"><strong>[보유 기간]</strong></p></div><div dir="auto" style="display:contents"><p class="" id="28ccbc69-4770-8031-adc5-e7e90d01885f">회사는 이용자의 동의를 받은 ‘보유 기간’ 또는 관련 법령에 따라 개인정보를 보유하고 있습니다.</p></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ccbc69-4770-808a-91a0-d2819c524018"><li style="list-style-type:disc">서비스마다 수집되는 개인정보 항목은 &lt;<a href="https://www.notion.so/287cbc694770801687d8ec0ff7a68aea?pvs=21">개인정보 처리 목적</a>&gt;에 기재된 내용을 통해 확인할 수 있습니다.</li></ul></div><div dir="auto" style="display:contents"><p class="" id="28ccbc69-4770-805b-8c70-debd8c1ef77c">각각의 보유 기간은 다음과 같습니다.</p></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ecbc69-4770-8060-a912-db94cf8cdccb"><li style="list-style-type:disc">회원 가입 및 관리 : 회사 앱(마이폴리) 탈퇴 시 까지이며, 다음 사유에 해당하는 경우에는 해당 사유 종료 시까지 보유합니다.<div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ecbc69-4770-80bc-b210-d6ada6e51c93"><li style="list-style-type:circle">관계 법령 위반에 따른 수사·조사 등이 진행 중인 경우 : 해당 수사·조사 종료 시까지</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ecbc69-4770-80e3-9b03-c4af8cc185e3"><li style="list-style-type:circle">서비스 이용에 따른 채권·채무관계 잔존 시 : 해당 채권·채무관계 정산 시까지</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ecbc69-4770-8002-bbfa-faaf12a26f4d"><li style="list-style-type:circle">1년간 서비스를 이용하지 않은 회원의 개인정보를 별도로 분리 보관 또는 삭제하고 있으며, 분리 보관된 개인정보는 3년간 보관 후 지체없이 파기합니다.</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ecbc69-4770-8022-8c96-c86cb2fb0eab"><li style="list-style-type:circle">부정이용 방지를 위한 회원 식별 정보(CI 등)는 탈퇴 후 3개월간 보관 후 지체없이 파기합니다. </li></ul></div></li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ecbc69-4770-802b-a9cb-eb05f07a226d"><li style="list-style-type:disc">재화 또는 서비스 제공 : 재화·서비스 공급 완료 및 요금 결제·정산 완료 시까지이며, 다음 사유에 해당하는 경우에는 해당 기간 종료 시까지 보유합니다.<div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ecbc69-4770-80ce-af61-f0792940e52c"><li style="list-style-type:circle">「전자상거래 등에서의 소비자 보호에 관한 법률」에 따른 표시·광고, 계약 내용 및 이행 등 거래에 관한 기록<div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ecbc69-4770-80a1-9072-c01a4ee2bce7"><li style="list-style-type:square">표시·광고에 관한 기록 : 6월</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ecbc69-4770-804f-8633-de809c52fbe8"><li style="list-style-type:square">계약 또는 청약 철회, 대금 결제, 재화 등의 공급 기록 : 5년</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ecbc69-4770-80b9-8b31-f812b426cf1f"><li style="list-style-type:square">소비자 불만 또는 분쟁 처리에 관한 기록 : 3년</li></ul></div></li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ecbc69-4770-808d-b6f8-fbc36b15260f"><li style="list-style-type:circle">「통신비밀보호법」 제 13조 내지 제 13조의 5에 따른 통신사실확인자료 보관<div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ecbc69-4770-80d3-8680-f1ba5b1b5e7c"><li style="list-style-type:square">가입자 전기통신일시, 개시·종료시간, 상대방 가입자번호, 사용도수, 발신기지국 위치추적자료 : 1년</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ecbc69-4770-8053-b8c4-fd1d618ec30d"><li style="list-style-type:square">컴퓨터 통신, 로그 기록 자료, 접속지 추적 자료 : 3개월</li></ul></div></li></ul></div></li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ecbc69-4770-800f-a015-ceb14233d263"><li style="list-style-type:disc">서비스 개선 및 신규 서비스 개발 : 회사 앱(마이폴리) 탈퇴 시, 단, 1년간 서비스를 이용하지 않은 회원의 개인정보를 별도로 분리 보관 또는 삭제하고 있으며, 분리 보관된 개인정보는 3년간 보관 후 지체없이 파기합니다.</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ecbc69-4770-80eb-8267-ea4a4317267e"><li style="list-style-type:disc">고충처리 : 고충처리 완료 시까지</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ecbc69-4770-80a2-a04c-d021b92776bb"><li style="list-style-type:disc">마케팅 및 광고 : 회사 앱(마이폴리) 탈퇴 시, 단, 1년간 서비스를 이용하지 않은 회원의 개인정보를 별도로 분리 보관 또는 삭제하고 있으며, 분리 보관된 개인정보는 3년간 보관 후 지체없이 파기합니다.</li></ul></div><div dir="auto" style="display:contents"><p class="" id="28ccbc69-4770-80c3-884c-d5f89aa29440">
</p></div><div dir="auto" style="display:contents"><p class="" id="28ccbc69-4770-8077-b23c-f883d2470854"><strong>[파기]</strong></p></div><div dir="auto" style="display:contents"><p class="" id="28ccbc69-4770-8026-a1c9-f31bdd23ec8a">1) 파기 절차</p></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ccbc69-4770-8018-9161-ddcae6bb02f7"><li style="list-style-type:disc">다음과 같은 파기 사유가 발생해 개인정보가 불필요하게 되었을 때는 지체 없이(5일 이내) 파기하고 있습니다.<div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ccbc69-4770-80c5-a1c8-ec86e54c67ed"><li style="list-style-type:circle">이용자가 동의를 철회하는 경우</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ccbc69-4770-80f7-b7f2-c9a713612cf0"><li style="list-style-type:circle">처리 목적을 달성한 경우</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ccbc69-4770-8008-84a2-d2813b7edc3b"><li style="list-style-type:circle">보유 기간이 경과한 경우</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ccbc69-4770-806d-991c-f0c55f21bac9"><li style="list-style-type:circle">해당 서비스가 폐지된 경우</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ccbc69-4770-8001-91f5-d0add97d16a6"><li style="list-style-type:circle">사업이 종료된 경우</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ccbc69-4770-80c0-aba3-c1ec1578f83a"><li style="list-style-type:circle">가명 정보의 처리 기간이 경과한 경우</li></ul></div></li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ccbc69-4770-80db-bc9c-f38102f0a063"><li style="list-style-type:disc">위 경우가 발생했음에도 관련 법령에 따라 계속 보존해야 하는 경우에는 별도 데이터베이스(DB)로 옮기거나 보관 장소를 달리하여 보존하고 있습니다.</li></ul></div><div dir="auto" style="display:contents"><p class="" id="28ccbc69-4770-800e-9752-ca48c9f4ffc9">2) 파기 방법</p></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ccbc69-4770-80a4-9e35-e2a8e5bfee92"><li style="list-style-type:disc">전자적 파일 형태로 저장된 개인정보는 관련 법령에 따라 복구되지 않도록 파기하며, 종이 문서에 기록된 개인정보는 세절하는 방법으로 파기하고 있습니다.</li></ul></div><div dir="auto" style="display:contents"><h3 class="" id="287cbc69-4770-8063-b842-cffb3f5d7855">3. 이용자 및 법정대리인의 권리 및 행사 방법</h3></div><div dir="auto" style="display:contents"><p class="" id="28ccbc69-4770-80f4-b0e0-cb807bc0c777">1) 이용자는 언제든지 다음 사항의 개인정보 보호 관련 권리를 행사할 수 있습니다.<div class="indented"><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ccbc69-4770-804c-9689-f76afc2a9015"><li style="list-style-type:disc">개인정보 열람 요구 및 통지 청구</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ccbc69-4770-805a-a10e-efd57fa67c03"><li style="list-style-type:disc">오류 등이 있을 경우 정정 청구</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ccbc69-4770-80c4-863c-d370d8a54da5"><li style="list-style-type:disc">삭제 요구 및 동의 철회 요구</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ccbc69-4770-80c0-9c97-d7e4288a770f"><li style="list-style-type:disc">처리 정지 요구</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ccbc69-4770-800b-8c2d-ffe73853bec4"><li style="list-style-type:disc">신용정보 이용 및 제공 사실 통보 요구</li></ul></div></div></p></div><div dir="auto" style="display:contents"><p class="" id="28ccbc69-4770-80b5-8744-fd38bca4a070">2) 앱을 통한 이용자의 권리 행사 절차는 다음과 같습니다.<div class="indented"><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ccbc69-4770-8089-9566-de7ef5a09eca"><li style="list-style-type:disc">개인정보 수정 : 로그인 &gt; 내정보 &gt; 프로필 수정</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ccbc69-4770-8025-9242-d48d25e46f90"><li style="list-style-type:disc">회원 탈퇴 : 로그인 &gt; 내정보 &gt; 설정 &gt; 회원탈퇴</li></ul></div></div></p></div><div dir="auto" style="display:contents"><p class="" id="28ccbc69-4770-805a-878e-cc9b211beb9e">3) 이용자가 개인정보 오류 등에 대한 정정 또는 삭제를 요구한 경우, 회사는 정정 또는 삭제를 완료할 때까지 개인정보를 이용하거나 제공하지 않습니다.</p></div><div dir="auto" style="display:contents"><p class="" id="28ccbc69-4770-80a7-a21e-c6c07d0184e9">4) 이용자에 따른 권리 행사는 이용자의 법정대리인이나 위임을 받은 자 등 대리인을 통하여 하실 수 있습니다. 이 경우 위임장을 제출하셔야 합니다.</p></div><div dir="auto" style="display:contents"><p class="" id="28ccbc69-4770-805f-bdc3-dd4095a8a4d5">5) 이용자는 개인정보 보호 유관 법령을 위반하여 회사가 처리하고 있는 이용자 본인이나 타인의 개인정보 및 사생활을 침해할 수 없습니다.</p></div><div dir="auto" style="display:contents"><p class="" id="28ccbc69-4770-807d-addc-f285bbf62175">
</p></div><div dir="auto" style="display:contents"><h3 class="" id="287cbc69-4770-80e1-b149-e9655dcd731a">4. 개인정보 안전성 확보 조치에 관한 사항</h3></div><div dir="auto" style="display:contents"><p class="" id="28ccbc69-4770-8025-8d28-e68a0d7d851b">회사는 개인정보의 안정성 확보를 위해 노력하고 있으며, 다음과 같은 조치를 취하고 있습니다.</p></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ccbc69-4770-80db-a399-d65dee1d7797"><li style="list-style-type:disc">관리적 조치 : 내부 관리 계획 수립, 시행, 정기적 직원 교육 등</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ccbc69-4770-80c8-b77e-d5a767184216"><li style="list-style-type:disc">기술적 조치 : 개인정보 처리 시스템 등의 접근 권한 관리, 접근통제시스템 설치, 고유식별정보 등의 암호화, 보안 프로그램 설치 등</li></ul></div><div dir="auto" style="display:contents"><p class="" id="28ccbc69-4770-80ef-95eb-f5c9385dd689">
</p></div><div dir="auto" style="display:contents"><h3 class="" id="287cbc69-4770-80d3-83c7-d5d1e9e2afdc">5. 개인정보보호 책임자 및 고충 처리 부서</h3></div><div dir="auto" style="display:contents"><p class="" id="28ccbc69-4770-809c-ae84-f9bdbaa1549f"><strong>[개인정보보호책임자]</strong></p></div><div dir="auto" style="display:contents"><p class="" id="28ccbc69-4770-8038-91b4-ec21e842f5e1">회사는 개인정보 처리에 관한 업무를 총괄해 책임지고, 개인정보 처리와 관련 정보 주체의 불만 처리 및 피해 구제 등을 위해 아래와 같이 개인정보보호책임자를 지정하고 있습니다.</p></div><div dir="auto" style="display:contents"><p class="" id="28ccbc69-4770-8082-9904-def8a894938a">1) 개인정보보호책임자</p></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ccbc69-4770-80cb-8f13-cbd74f93de43"><li style="list-style-type:disc">성명 : 정병준</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ccbc69-4770-80b9-ab5e-e5d8e5345059"><li style="list-style-type:disc">연락처 : wjd777486@gmail.com</li></ul></div><div dir="auto" style="display:contents"><p class="" id="28ccbc69-4770-8022-9ab0-d3b50767cd08">2) 개인정보보호 담당 부서</p></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ccbc69-4770-8010-8e9f-dc547156c6de"><li style="list-style-type:disc">성명 : 정병준</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ccbc69-4770-8071-bd13-f75e54fdfe3e"><li style="list-style-type:disc">연락처 : wjd777486@gmail.com</li></ul></div><div dir="auto" style="display:contents"><p class="" id="28ccbc69-4770-80c8-91c6-fdc4e3ea1364">
</p></div><div dir="auto" style="display:contents"><p class="" id="28ccbc69-4770-8063-bbb5-d551e15578f3"><strong>[개인정보 열람청구]</strong></p></div><div dir="auto" style="display:contents"><p class="" id="28ccbc69-4770-8079-99f7-c757194f6a57">정보 주체는 개인정보보호법 제 35조에 따른 개인정보 열람청구를 아래의 부서에 요청할 수 있습니다. 회사는 정보 주체의 개인정보 열람청구가 신속히 처리될 수 있도록 노력하고 있습니다.</p></div><div dir="auto" style="display:contents"><p class="" id="28ccbc69-4770-8075-b6f0-f614a46556e2">☐ 개인정보 열람청구 접수 및 처리 부서</p></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ccbc69-4770-8039-9a65-ca403334af63"><li style="list-style-type:disc">성명 : 정병준</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ccbc69-4770-8050-8831-de580b24f947"><li style="list-style-type:disc">연락처 : wjd777486@gmail.com</li></ul></div><div dir="auto" style="display:contents"><p class="" id="28ccbc69-4770-8063-a151-f29ac24a6a1c">
</p></div><div dir="auto" style="display:contents"><p class="" id="28ccbc69-4770-80c0-8f3d-f3e330280551"><strong>[권익침해 구제방법]</strong></p></div><div dir="auto" style="display:contents"><p class="" id="28ccbc69-4770-804a-9f83-d0e051df9a21">정보 주체는 아래의 기관에 대해 개인정보 침해에 대한 피해구제, 상담 등을 문의할 수 있습니다. 아래의 기관은 회사와 별개의 기관으로서, 회사의 자체적인 개인정보 불만 처리, 피해구제 결과에 만족하지 못하시거나 보다 자세한 도움이 필요하면 문의해주시기 바랍니다.</p></div><div dir="ltr" style="display:contents"><table class="simple-table" id="755b2575-6857-44c9-8691-8251b2a57516"><thead class="simple-table-header"><div dir="ltr" style="display:contents"><tr id="63676d02-b783-4639-a2b6-d1dd0344f58e"><th class="simple-table-header-color simple-table-header" id="\]hB" style="width:253px"><strong>기관</strong></th><th class="simple-table-header-color simple-table-header" id="~UkH" style="width:168.6015625px"><strong>연락처</strong></th><th class="simple-table-header-color simple-table-header" id="kUP_" style="width:279.4375px"><strong>사이트</strong></th></tr></div></thead><tbody><div dir="ltr" style="display:contents"><tr id="265b9a3a-ece1-45cf-b485-bedcf646e35c"><td class="" id="\]hB" style="width:253px">개인정보침해 신고센터</td><td class="" id="~UkH" style="width:168.6015625px">(국번없이) 118</td><td class="" id="kUP_" style="width:279.4375px"><strong><a href="https://privacy.kisa.or.kr/main.do">privacy.kisa.or.kr</a></strong></td></tr></div><div dir="ltr" style="display:contents"><tr id="13ca62f2-f628-4f36-8744-377f6981d5d2"><td class="" id="\]hB" style="width:253px">개인정보 분쟁조정위원회</td><td class="" id="~UkH" style="width:168.6015625px">1833-6972</td><td class="" id="kUP_" style="width:279.4375px"><strong><a href="https://www.kopico.go.kr/main/main.do">www.kopico.go.kr</a></strong></td></tr></div><div dir="ltr" style="display:contents"><tr id="84da7c93-f8ca-4d6c-8522-d758b4c16df2"><td class="" id="\]hB" style="width:253px">대검찰청 사이버수사과</td><td class="" id="~UkH" style="width:168.6015625px">(국번없이) 1301</td><td class="" id="kUP_" style="width:279.4375px"><strong><a href="https://www.spo.go.kr/site/spo/main.do">www.spo.go.kr</a></strong></td></tr></div><div dir="ltr" style="display:contents"><tr id="d9cd32f8-f23c-4c09-b36a-46e5fb979b3d"><td class="" id="\]hB" style="width:253px">경찰청 사이버안전국(경찰민원콜센터)</td><td class="" id="~UkH" style="width:168.6015625px">(국번없이) 182</td><td class="" id="kUP_" style="width:279.4375px"><strong><a href="https://toss.im/ecrm.police.go.kr">ecrm.police.go.kr</a></strong></td></tr></div></tbody></table></div><div dir="auto" style="display:contents"><h3 class="" id="287cbc69-4770-80a5-99af-e9716c2c4b2e">6. 개정 전 고지 의무 등 안내</h3></div><div dir="auto" style="display:contents"><p class="" id="287cbc69-4770-80b0-b5f3-f7846088d329">본 개인정보 처리방침을 개정할 경우, 앱 메인 화면 등을 통해 변경 내용을 공지하고 있습니다.</p></div><div dir="auto" style="display:contents"><p class="" id="28ccbc69-4770-8090-95ae-d246b5ddc3d0">
</p></div><div dir="auto" style="display:contents"><hr id="28ccbc69-4770-8020-905e-cbc63a0a390c"/></div><div dir="auto" style="display:contents"><p class="" id="28ccbc69-4770-80c8-b28a-c06befbff952">공고일자 : 2025.10.15</p></div><div dir="auto" style="display:contents"><p class="" id="28ccbc69-4770-80c2-861d-f66be893763c">시행일자 : 2026.1.15</p></div><div dir="auto" style="display:contents"><p class="" id="28ccbc69-4770-8068-bc34-ccc5bf4dd280">
</p></div><div dir="auto" style="display:contents"><p class="" id="301cbc69-4770-8039-b71c-ca8b01a2ac1d">
</p></div></div>
</article>
  $privacy_policy$,
  1,
  TRUE,
  DATE '2025-09-01'
)
ON CONFLICT (name, version) DO NOTHING;

-- 광고성 정보 수신 동의 (version=1, required=false)
INSERT INTO terms (name, title, content, version, required, effective_from)
VALUES (
  'MARKETING_CONSENT',
  '광고성 정보 수신 동의',
  $marketing_consent$
<style>
/* cspell:disable-file */
/* webkit printing magic: print all background colors */
html {
	-webkit-print-color-adjust: exact;
}
* {
	box-sizing: border-box;
	-webkit-print-color-adjust: exact;
}

html,
body {
	margin: 0;
	padding: 0;
}
@media only screen {
	body {
		margin: 2em auto;
		max-width: 900px;
		color: rgb(55, 53, 47);
	}
}

body {
	line-height: 1.5;
	white-space: pre-wrap;
}

a,
a.visited {
	color: inherit;
	text-decoration: underline;
}

.pdf-relative-link-path {
	font-size: 80%;
	color: #444;
}

h1,
h2,
h3 {
	letter-spacing: -0.01em;
	line-height: 1.2;
	font-weight: 600;
	margin-bottom: 0;
}

/* Override strong tags inside headings to maintain consistent weight */
h1 strong,
h2 strong,
h3 strong {
	font-weight: 600;
}

.page-title {
	font-size: 2.5rem;
	font-weight: 700;
	margin-top: 0;
	margin-bottom: 0.75em;
}

h1 {
	font-size: 1.875rem;
	margin-top: 1.875rem;
}

h2 {
	font-size: 1.5rem;
	margin-top: 1.5rem;
}

h3 {
	font-size: 1.25rem;
	margin-top: 1.25rem;
}

.source {
	border: 1px solid #ddd;
	border-radius: 3px;
	padding: 1.5em;
	word-break: break-all;
}

.callout {
	border-radius: 10px;
	padding: 1rem;
}

figure {
	margin: 1.25em 0;
	page-break-inside: avoid;
}

figcaption {
	opacity: 0.5;
	font-size: 85%;
	margin-top: 0.5em;
}

mark {
	background-color: transparent;
}

.indented {
	padding-left: 1.5em;
}

hr {
	background: transparent;
	display: block;
	width: 100%;
	height: 1px;
	visibility: visible;
	border: none;
	border-bottom: 1px solid rgba(55, 53, 47, 0.09);
}

img {
	max-width: 100%;
}

@media only print {
	img {
		max-height: 100vh;
		object-fit: contain;
	}
}

@page {
	margin: 1in;
}

.collection-content {
	font-size: 0.875rem;
}

.collection-content td {
	white-space: pre-wrap;
	word-break: break-word;
}

.column-list {
	display: flex;
	justify-content: space-between;
}

.column {
	padding: 0 1em;
}

.column:first-child {
	padding-left: 0;
}

.column:last-child {
	padding-right: 0;
}

.table_of_contents-item {
	display: block;
	font-size: 0.875rem;
	line-height: 1.3;
	padding: 0.125rem;
}

.table_of_contents-indent-1 {
	margin-left: 1.5rem;
}

.table_of_contents-indent-2 {
	margin-left: 3rem;
}

.table_of_contents-indent-3 {
	margin-left: 4.5rem;
}

.table_of_contents-link {
	text-decoration: none;
	opacity: 0.7;
	border-bottom: 1px solid rgba(55, 53, 47, 0.18);
}

table,
th,
td {
	border: 1px solid rgba(55, 53, 47, 0.09);
	border-collapse: collapse;
}

table {
	border-left: none;
	border-right: none;
}

th,
td {
	font-weight: normal;
	padding: 0.25em 0.5em;
	line-height: 1.5;
	min-height: 1.5em;
	text-align: left;
}

th {
	color: rgba(55, 53, 47, 0.6);
}

ol,
ul {
	margin: 0;
	margin-block-start: 0.6em;
	margin-block-end: 0.6em;
}

li > ol:first-child,
li > ul:first-child {
	margin-block-start: 0.6em;
}

ul > li {
	list-style: disc;
}

ul.to-do-list {
	padding-inline-start: 0;
}

ul.to-do-list > li {
	list-style: none;
}

.to-do-children-checked {
	text-decoration: line-through;
	opacity: 0.375;
}

ul.toggle > li {
	list-style: none;
}

ul {
	padding-inline-start: 1.7em;
}

ul > li {
	padding-left: 0.1em;
}

ol {
	padding-inline-start: 1.6em;
}

ol > li {
	padding-left: 0.2em;
}

.mono ol {
	padding-inline-start: 2em;
}

.mono ol > li {
	text-indent: -0.4em;
}

.toggle {
	padding-inline-start: 0em;
	list-style-type: none;
}

/* Indent toggle children */
.toggle > li > details {
	padding-left: 1.7em;
}

.toggle > li > details > summary {
	margin-left: -1.1em;
}

.selected-value {
	display: inline-block;
	padding: 0 0.5em;
	background: rgba(206, 205, 202, 0.5);
	border-radius: 3px;
	margin-right: 0.5em;
	margin-top: 0.3em;
	margin-bottom: 0.3em;
	white-space: nowrap;
}

.collection-title {
	display: inline-block;
	margin-right: 1em;
}

.page-description {
	margin-bottom: 2em;
}

.simple-table {
	margin-top: 1em;
	font-size: 0.875rem;
	empty-cells: show;
}
.simple-table td {
	height: 29px;
	min-width: 120px;
}

.simple-table th {
	height: 29px;
	min-width: 120px;
}

.simple-table-header-color {
	background: rgb(247, 246, 243);
	color: black;
}
.simple-table-header {
	font-weight: 500;
}

time {
	opacity: 0.5;
}

.icon {
	display: inline-flex;
	align-items: center;
	justify-content: center;
	max-width: 1.2em;
	max-height: 1.2em;
	text-decoration: none;
	vertical-align: text-bottom;
	margin-right: 0.5em;
}

img.icon {
	border-radius: 3px;
}

.callout img.notion-static-icon {
	width: 1em;
	height: 1em;
}

.callout p {
	margin: 0;
}

.callout h1,
.callout h2,
.callout h3 {
	margin: 0 0 0.6rem;
}

.user-icon {
	width: 1.5em;
	height: 1.5em;
	border-radius: 100%;
	margin-right: 0.5rem;
}

.user-icon-inner {
	font-size: 0.8em;
}

.text-icon {
	border: 1px solid #000;
	text-align: center;
}

.page-cover-image {
	display: block;
	object-fit: cover;
	width: 100%;
	max-height: 30vh;
}

.page-header-icon {
	font-size: 3rem;
	margin-bottom: 1rem;
}

.page-header-icon-with-cover {
	margin-top: -0.72em;
	margin-left: 0.07em;
}

.page-header-icon img {
	border-radius: 3px;
}

.link-to-page {
	margin: 1em 0;
	padding: 0;
	border: none;
	font-weight: 500;
}

p > .user {
	opacity: 0.5;
}

td > .user,
td > time {
	white-space: nowrap;
}

input[type="checkbox"] {
	transform: scale(1.5);
	margin-right: 0.6em;
	vertical-align: middle;
}

p {
	margin-top: 0.5em;
	margin-bottom: 0.5em;
}

.image {
	border: none;
	margin: 1.5em 0;
	padding: 0;
	border-radius: 0;
	text-align: center;
}

.code,
code {
	background: rgba(135, 131, 120, 0.15);
	border-radius: 3px;
	padding: 0.2em 0.4em;
	border-radius: 3px;
	font-size: 85%;
	tab-size: 2;
}

code {
	color: #eb5757;
}

.code {
	padding: 1.5em 1em;
}

.code-wrap {
	white-space: pre-wrap;
	word-break: break-all;
}

.code > code {
	background: none;
	padding: 0;
	font-size: 100%;
	color: inherit;
}

blockquote {
	font-size: 1em;
	margin: 1em 0;
	padding-left: 1em;
	border-left: 3px solid rgb(55, 53, 47);
}

blockquote.quote-large {
	font-size: 1.25em;
}

.bookmark {
	text-decoration: none;
	max-height: 8em;
	padding: 0;
	display: flex;
	width: 100%;
	align-items: stretch;
}

.bookmark-title {
	font-size: 0.85em;
	overflow: hidden;
	text-overflow: ellipsis;
	height: 1.75em;
	white-space: nowrap;
}

.bookmark-text {
	display: flex;
	flex-direction: column;
}

.bookmark-info {
	flex: 4 1 180px;
	padding: 12px 14px 14px;
	display: flex;
	flex-direction: column;
	justify-content: space-between;
}

.bookmark-image {
	width: 33%;
	flex: 1 1 180px;
	display: block;
	position: relative;
	object-fit: cover;
	border-radius: 1px;
}

.bookmark-description {
	color: rgba(55, 53, 47, 0.6);
	font-size: 0.75em;
	overflow: hidden;
	max-height: 4.5em;
	word-break: break-word;
}

.bookmark-href {
	font-size: 0.75em;
	margin-top: 0.25em;
}

.sans { font-family: ui-sans-serif, -apple-system, BlinkMacSystemFont, "Segoe UI Variable Display", "Segoe UI", Helvetica, "Apple Color Emoji", "Noto Sans Arabic", "Noto Sans Hebrew", Arial, sans-serif, "Segoe UI Emoji", "Segoe UI Symbol"; }
.code { font-family: "SFMono-Regular", Menlo, Consolas, "PT Mono", "Liberation Mono", Courier, monospace; }
.serif { font-family: Lyon-Text, Georgia, ui-serif, serif; }
.mono { font-family: iawriter-mono, Nitti, Menlo, Courier, monospace; }
.pdf .sans { font-family: Inter, ui-sans-serif, -apple-system, BlinkMacSystemFont, "Segoe UI Variable Display", "Segoe UI", Helvetica, "Apple Color Emoji", "Noto Sans Arabic", "Noto Sans Hebrew", Arial, sans-serif, "Segoe UI Emoji", "Segoe UI Symbol", 'Twemoji', 'Noto Color Emoji', 'Noto Sans CJK JP'; }
.pdf:lang(zh-CN) .sans { font-family: Inter, ui-sans-serif, -apple-system, BlinkMacSystemFont, "Segoe UI Variable Display", "Segoe UI", Helvetica, "Apple Color Emoji", "Noto Sans Arabic", "Noto Sans Hebrew", Arial, sans-serif, "Segoe UI Emoji", "Segoe UI Symbol", 'Twemoji', 'Noto Color Emoji', 'Noto Sans CJK SC'; }
.pdf:lang(zh-TW) .sans { font-family: Inter, ui-sans-serif, -apple-system, BlinkMacSystemFont, "Segoe UI Variable Display", "Segoe UI", Helvetica, "Apple Color Emoji", "Noto Sans Arabic", "Noto Sans Hebrew", Arial, sans-serif, "Segoe UI Emoji", "Segoe UI Symbol", 'Twemoji', 'Noto Color Emoji', 'Noto Sans CJK TC'; }
.pdf:lang(ko-KR) .sans { font-family: Inter, ui-sans-serif, -apple-system, BlinkMacSystemFont, "Segoe UI Variable Display", "Segoe UI", Helvetica, "Apple Color Emoji", "Noto Sans Arabic", "Noto Sans Hebrew", Arial, sans-serif, "Segoe UI Emoji", "Segoe UI Symbol", 'Twemoji', 'Noto Color Emoji', 'Noto Sans CJK KR'; }
.pdf .code { font-family: Source Code Pro, "SFMono-Regular", Menlo, Consolas, "PT Mono", "Liberation Mono", Courier, monospace, 'Twemoji', 'Noto Color Emoji', 'Noto Sans Mono CJK JP'; }
.pdf:lang(zh-CN) .code { font-family: Source Code Pro, "SFMono-Regular", Menlo, Consolas, "PT Mono", "Liberation Mono", Courier, monospace, 'Twemoji', 'Noto Color Emoji', 'Noto Sans Mono CJK SC'; }
.pdf:lang(zh-TW) .code { font-family: Source Code Pro, "SFMono-Regular", Menlo, Consolas, "PT Mono", "Liberation Mono", Courier, monospace, 'Twemoji', 'Noto Color Emoji', 'Noto Sans Mono CJK TC'; }
.pdf:lang(ko-KR) .code { font-family: Source Code Pro, "SFMono-Regular", Menlo, Consolas, "PT Mono", "Liberation Mono", Courier, monospace, 'Twemoji', 'Noto Color Emoji', 'Noto Sans Mono CJK KR'; }
.pdf .serif { font-family: PT Serif, Lyon-Text, Georgia, ui-serif, serif, 'Twemoji', 'Noto Color Emoji', 'Noto Serif CJK JP'; }
.pdf:lang(zh-CN) .serif { font-family: PT Serif, Lyon-Text, Georgia, ui-serif, serif, 'Twemoji', 'Noto Color Emoji', 'Noto Serif CJK SC'; }
.pdf:lang(zh-TW) .serif { font-family: PT Serif, Lyon-Text, Georgia, ui-serif, serif, 'Twemoji', 'Noto Color Emoji', 'Noto Serif CJK TC'; }
.pdf:lang(ko-KR) .serif { font-family: PT Serif, Lyon-Text, Georgia, ui-serif, serif, 'Twemoji', 'Noto Color Emoji', 'Noto Serif CJK KR'; }
.pdf .mono { font-family: PT Mono, iawriter-mono, Nitti, Menlo, Courier, monospace, 'Twemoji', 'Noto Color Emoji', 'Noto Sans Mono CJK JP'; }
.pdf:lang(zh-CN) .mono { font-family: PT Mono, iawriter-mono, Nitti, Menlo, Courier, monospace, 'Twemoji', 'Noto Color Emoji', 'Noto Sans Mono CJK SC'; }
.pdf:lang(zh-TW) .mono { font-family: PT Mono, iawriter-mono, Nitti, Menlo, Courier, monospace, 'Twemoji', 'Noto Color Emoji', 'Noto Sans Mono CJK TC'; }
.pdf:lang(ko-KR) .mono { font-family: PT Mono, iawriter-mono, Nitti, Menlo, Courier, monospace, 'Twemoji', 'Noto Color Emoji', 'Noto Sans Mono CJK KR'; }
.highlight-default {
	color: rgba(44, 44, 43, 1);
}
.highlight-gray {
	color: rgba(125, 122, 117, 1);
	fill: rgba(125, 122, 117, 1);
}
.highlight-brown {
	color: rgba(159, 118, 90, 1);
	fill: rgba(159, 118, 90, 1);
}
.highlight-orange {
	color: rgba(210, 123, 45, 1);
	fill: rgba(210, 123, 45, 1);
}
.highlight-yellow {
	color: rgba(203, 148, 52, 1);
	fill: rgba(203, 148, 52, 1);
}
.highlight-teal {
	color: rgba(80, 148, 110, 1);
	fill: rgba(80, 148, 110, 1);
}
.highlight-blue {
	color: rgba(56, 125, 201, 1);
	fill: rgba(56, 125, 201, 1);
}
.highlight-purple {
	color: rgba(154, 107, 180, 1);
	fill: rgba(154, 107, 180, 1);
}
.highlight-pink {
	color: rgba(193, 76, 138, 1);
	fill: rgba(193, 76, 138, 1);
}
.highlight-red {
	color: rgba(207, 81, 72, 1);
	fill: rgba(207, 81, 72, 1);
}
.highlight-default_background {
	color: rgba(44, 44, 43, 1);
}
.highlight-gray_background {
	background: rgba(42, 28, 0, 0.07);
}
.highlight-brown_background {
	background: rgba(139, 46, 0, 0.086);
}
.highlight-orange_background {
	background: rgba(224, 101, 1, 0.129);
}
.highlight-yellow_background {
	background: rgba(211, 168, 0, 0.137);
}
.highlight-teal_background {
	background: rgba(0, 100, 45, 0.09);
}
.highlight-blue_background {
	background: rgba(0, 124, 215, 0.094);
}
.highlight-purple_background {
	background: rgba(102, 0, 178, 0.078);
}
.highlight-pink_background {
	background: rgba(197, 0, 93, 0.086);
}
.highlight-red_background {
	background: rgba(223, 22, 0, 0.094);
}
.block-color-default {
	color: inherit;
	fill: inherit;
}
.block-color-gray {
	color: rgba(125, 122, 117, 1);
	fill: rgba(125, 122, 117, 1);
}
.block-color-brown {
	color: rgba(159, 118, 90, 1);
	fill: rgba(159, 118, 90, 1);
}
.block-color-orange {
	color: rgba(210, 123, 45, 1);
	fill: rgba(210, 123, 45, 1);
}
.block-color-yellow {
	color: rgba(203, 148, 52, 1);
	fill: rgba(203, 148, 52, 1);
}
.block-color-teal {
	color: rgba(80, 148, 110, 1);
	fill: rgba(80, 148, 110, 1);
}
.block-color-blue {
	color: rgba(56, 125, 201, 1);
	fill: rgba(56, 125, 201, 1);
}
.block-color-purple {
	color: rgba(154, 107, 180, 1);
	fill: rgba(154, 107, 180, 1);
}
.block-color-pink {
	color: rgba(193, 76, 138, 1);
	fill: rgba(193, 76, 138, 1);
}
.block-color-red {
	color: rgba(207, 81, 72, 1);
	fill: rgba(207, 81, 72, 1);
}
.block-color-default_background {
	color: inherit;
	fill: inherit;
}
.block-color-gray_background {
	background: rgba(240, 239, 237, 1);
}
.block-color-brown_background {
	background: rgba(245, 237, 233, 1);
}
.block-color-orange_background {
	background: rgba(251, 235, 222, 1);
}
.block-color-yellow_background {
	background: rgba(249, 243, 220, 1);
}
.block-color-teal_background {
	background: rgba(232, 241, 236, 1);
}
.block-color-blue_background {
	background: rgba(229, 242, 252, 1);
}
.block-color-purple_background {
	background: rgba(243, 235, 249, 1);
}
.block-color-pink_background {
	background: rgba(250, 233, 241, 1);
}
.block-color-red_background {
	background: rgba(252, 233, 231, 1);
}
.select-value-color-default { background-color: rgba(42, 28, 0, 0.07); }
.select-value-color-gray { background-color: rgba(28, 19, 1, 0.11); }
.select-value-color-brown { background-color: rgba(127, 51, 0, 0.156); }
.select-value-color-orange { background-color: rgba(196, 88, 0, 0.203); }
.select-value-color-yellow { background-color: rgba(209, 156, 0, 0.282); }
.select-value-color-green { background-color: rgba(0, 96, 38, 0.156); }
.select-value-color-blue { background-color: rgba(0, 118, 217, 0.203); }
.select-value-color-purple { background-color: rgba(92, 0, 163, 0.141); }
.select-value-color-pink { background-color: rgba(183, 0, 78, 0.152); }
.select-value-color-red { background-color: rgba(206, 24, 0, 0.164); }

.checkbox {
	display: inline-flex;
	vertical-align: text-bottom;
	width: 16;
	height: 16;
	background-size: 16px;
	margin-left: 2px;
	margin-right: 5px;
}

.checkbox-on {
	background-image: url("data:image/svg+xml;charset=UTF-8,%3Csvg%20width%3D%2216%22%20height%3D%2216%22%20viewBox%3D%220%200%2016%2016%22%20fill%3D%22none%22%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%3E%0A%3Crect%20width%3D%2216%22%20height%3D%2216%22%20fill%3D%22%2358A9D7%22%2F%3E%0A%3Cpath%20d%3D%22M6.71429%2012.2852L14%204.9995L12.7143%203.71436L6.71429%209.71378L3.28571%206.2831L2%207.57092L6.71429%2012.2852Z%22%20fill%3D%22white%22%2F%3E%0A%3C%2Fsvg%3E");
}

.checkbox-off {
	background-image: url("data:image/svg+xml;charset=UTF-8,%3Csvg%20width%3D%2216%22%20height%3D%2216%22%20viewBox%3D%220%200%2016%2016%22%20fill%3D%22none%22%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%3E%0A%3Crect%20x%3D%220.75%22%20y%3D%220.75%22%20width%3D%2214.5%22%20height%3D%2214.5%22%20fill%3D%22white%22%20stroke%3D%22%2336352F%22%20stroke-width%3D%221.5%22%2F%3E%0A%3C%2Fsvg%3E");
}
	
</style>
<article class="page sans">
<div class="page-body"><div dir="auto" style="display:contents"><h3 class="" id="28ecbc69-4770-8087-83bb-ed9be29e2720">광고성 정보 수신 동의</h3></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ecbc69-4770-80d7-8195-d8bb20aaa140"><li style="list-style-type:disc">광고성 정보 매체는 다음과 같습니다.<div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ecbc69-4770-8099-93a1-de678aceb9d0"><li style="list-style-type:circle">전화</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ecbc69-4770-8021-be0f-dcc10b668d5b"><li style="list-style-type:circle">휴대폰 메시지(모바일 메시지 포함)</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ecbc69-4770-80a5-83cc-d5a0acc3a467"><li style="list-style-type:circle">앱푸시</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ecbc69-4770-8089-b039-ec37c41d332e"><li style="list-style-type:circle">이메일</li></ul></div></li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ecbc69-4770-80e7-91a4-c16981f94331"><li style="list-style-type:disc">마이폴리(이하 “회사”)는고객님이 수집 및 이용에 동의한 개인정보를 이용하여 전화, 앱푸시, 이메일 등 전자적 전송 매체를 통해 다양한 이벤트•혜택 제공 등 마케팅을 목적으로 연락할 수 있습니다.</li></ul></div><div dir="auto" style="display:contents"><ul class="bulleted-list" id="28ecbc69-4770-8044-9d40-c135ec0999f2"><li style="list-style-type:disc">광고성 정보 수신 여부는 [설정] &gt; [알림 설정] 메뉴에서 언제든지 변경할 수 있습니다.<br/>* 단, 수신 거부를 처리하는 데 1~2일이 소요될 수 있습니다.</li></ul></div><div dir="auto" style="display:contents"><p class="" id="28ecbc69-4770-8066-8de7-c3a58aedcdf6">
</p></div><div dir="auto" style="display:contents"><hr id="28ecbc69-4770-80e2-aa4c-c32baf6a7399"/></div><div dir="auto" style="display:contents"><p class="" id="28ecbc69-4770-80c8-b053-cc7f535868ff">공고일자 : 2025.10.16</p></div><div dir="auto" style="display:contents"><p class="" id="28ecbc69-4770-804c-b534-eca9e0ba3460">시행일자 : 2026.2.1</p></div></div>
</article>
  $marketing_consent$,
  1,
  FALSE,
  NULL
)
ON CONFLICT (name, version) DO NOTHING;
