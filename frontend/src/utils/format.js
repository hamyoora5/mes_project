export function formatDateTime(value) {
  if (!value) {
    return "-";
  }
  return new Intl.DateTimeFormat("ko-KR", {
    dateStyle: "short",
    timeStyle: "short"
  }).format(new Date(value));
}
